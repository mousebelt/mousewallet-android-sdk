package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.PaymentItem;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.interfaces.BRAuthCompletion;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRReportsManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRSharedPrefs;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRCurrency;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRExchange;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRWalletManager;
import java.math.BigDecimal;
import java.util.Locale;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 4/25/17.
 * Copyright (c) 2017 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class BRSender {
    private static final String TAG = BRSender.class.getName();

    private static BRSender instance;

    private BRSender() {
    }

    public static BRSender getInstance() {
        if (instance == null) instance = new BRSender();
        return instance;
    }

    /**
     * Create tx from the PaymentItem object and try to send it
     */
    public void sendTransaction(final Context app, final PaymentItem request) {
        //array in order to be able to modify the first element from an inner block (can't be final)
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    tryPay(app, request);
                    return; //return so no error is shown
                } catch (InsufficientFundsException ignored) {
//                    errMessage[0] = app.getString(R.string.insufficient_funds);
                } catch (AmountSmallerThanMinException e) {
                    long minAmount = BRWalletManager.getInstance().getMinOutputAmountRequested();
                } catch (SpendingNotAllowed spendingNotAllowed) {
                    showSpendNotAllowed(app);
                    return;
                } catch (FeeNeedsAdjust feeNeedsAdjust) {
                    //offer to change amount, so it would be enough for fee
//                    showFailed(app); //just show failed for now
                    showAdjustFee((Activity) app, request);
                    return;
                }
            }
        });

    }

    /**
     * Try transaction and throw appropriate exceptions if something was wrong
     * BLOCKS
     */
    public void tryPay(final Context app, final PaymentItem paymentRequest) throws InsufficientFundsException,
            AmountSmallerThanMinException, SpendingNotAllowed, FeeNeedsAdjust {
        if (paymentRequest == null || paymentRequest.addresses == null) {
            Log.e(TAG, "handlePay: WRONG PARAMS");
            String message = paymentRequest == null ? "paymentRequest is null" : "addresses is null";
            BRReportsManager.reportBug(new RuntimeException("paymentRequest is malformed: " + message), true);
        }
        long amount = paymentRequest.amount;
        long balance = BRWalletManager.getInstance().getBalance(app);
        final BRWalletManager m = BRWalletManager.getInstance();
        long minOutputAmount = BRWalletManager.getInstance().getMinOutputAmount();
        final long maxOutputAmount = BRWalletManager.getInstance().getMaxOutputAmount();

        // check if spending is allowed
        if (!BRSharedPrefs.getAllowSpend(app)) {
            throw new SpendingNotAllowed();
        }

        //check if amount isn't smaller than the min amount
        if (isSmallerThanMin(app, paymentRequest)) {
            throw new AmountSmallerThanMinException(amount, balance);
        }

        //amount is larger than balance
        if (isLargerThanBalance(app, paymentRequest)) {
            throw new InsufficientFundsException(amount, balance);
        }

        //not enough for fee
        if (notEnoughForFee(app, paymentRequest)) {
            //weird bug when the core BRWalletManager is NULL
            if (maxOutputAmount == -1) {
                BRReportsManager.reportBug(new RuntimeException("getMaxOutputAmount is -1, meaning _wallet is NULL"), true);
            }
            // max you can spend is smaller than the min you can spend
            if (maxOutputAmount < minOutputAmount) {
                throw new InsufficientFundsException(amount, balance);
            }

            long feeForTx = m.feeForTransaction(paymentRequest.addresses[0], paymentRequest.amount);
            throw new FeeNeedsAdjust(amount, balance, feeForTx);
        }
        // payment successful
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                byte[] tmpTx = m.tryTransaction(paymentRequest.addresses[0], paymentRequest.amount);
                if (tmpTx == null) {
                    //something went wrong, failed to create tx
                    ((Activity) app).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    return;
                }
                paymentRequest.serializedTx = tmpTx;
                PostAuth.getInstance().setPaymentItem(paymentRequest);
//                confirmPay(app, paymentRequest);
            }
        });

    }

    private void showAdjustFee(final Activity app, PaymentItem item) {
        BRWalletManager m = BRWalletManager.getInstance();
        long maxAmountDouble = m.getMaxOutputAmount();
        if (maxAmountDouble == -1) {
            BRReportsManager.reportBug(new RuntimeException("getMaxOutputAmount is -1, meaning _wallet is NULL"));
            return;
        }
        if (maxAmountDouble == 0) {
        } else {
//            long fee = m.feeForTransaction(item.addresses[0], maxAmountDouble);
//            feeForTx += (m.getBalance(app) - request.amount) % 100;
//            BRDialog.showCustomDialog(app, app.getString(R.string.Alerts_sendFailure), "Insufficient amount for transaction fee", app.getString(R.string.Button_ok), null, new BRDialogView.BROnClickListener() {
//                @Override
//                public void onClick(BRDialogView brDialogView) {
//                    brDialogView.dismissWithAnimation();
//                }
//            }, null, null, 0);
            //todo fix this fee adjustment
        }

    }


    public void confirmPay(final Context ctx, final PaymentItem request) {
        if (ctx == null) {
            Log.e(TAG, "confirmPay: context is null");
            return;
        }

        String message = createConfirmation(ctx, request);

        double minOutput;
        if (request.isAmountRequested) {
            minOutput = BRWalletManager.getInstance().getMinOutputAmountRequested();
        } else {
            minOutput = BRWalletManager.getInstance().getMinOutputAmount();
        }

        //amount can't be less than the min
        if (request.amount < minOutput) {

            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            return;
        }
        boolean forcePin = false;

        Log.e(TAG, "confirmPay: totalSent: " + BRWalletManager.getInstance().getTotalSent());
        Log.e(TAG, "confirmPay: request.amount: " + request.amount);
        Log.e(TAG, "confirmPay: total limit: " + AuthManager.getInstance().getTotalLimit(ctx));
        Log.e(TAG, "confirmPay: limit: " + BRKeyStore.getSpendLimit(ctx));

        if (BRWalletManager.getInstance().getTotalSent() + request.amount > AuthManager.getInstance().getTotalLimit(ctx)) {
            forcePin = true;
        }

        //successfully created the transaction, authenticate user
        AuthManager.getInstance().authPrompt(ctx, "", message, forcePin, new BRAuthCompletion() {
            @Override
            public void onComplete() {
                BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                    @Override
                    public void run() {
                        PostAuth.getInstance().onPublishTxAuth(ctx, true);
                        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });

                    }
                });

            }

            @Override
            public void onCancel() {
                //nothing
            }
        });

    }

    public String createConfirmation(Context ctx, PaymentItem request) {
        String receiver = getReceiver(request);

        String iso = BRSharedPrefs.getIso(ctx);

        BRWalletManager m = BRWalletManager.getInstance();
        long feeForTx = m.feeForTransaction(request.addresses[0], request.amount);
        if (feeForTx == 0) {
            long maxAmount = m.getMaxOutputAmount();
            if (maxAmount == -1) {
                BRReportsManager.reportBug(new RuntimeException("getMaxOutputAmount is -1, meaning _wallet is NULL"),true);
            }
            if (maxAmount == 0) {
                return null;
            }
            feeForTx = m.feeForTransaction(request.addresses[0], maxAmount);
            feeForTx += (BRWalletManager.getInstance().getBalance(ctx) - request.amount) % 100;
        }
        final long total = request.amount + feeForTx;
        String formattedAmountBTC = BRCurrency.getFormattedCurrencyString(ctx, "LTC", BRExchange.getBitcoinForSatoshis(ctx, new BigDecimal(request.amount)));
        String formattedFeeBTC = BRCurrency.getFormattedCurrencyString(ctx, "LTC", BRExchange.getBitcoinForSatoshis(ctx, new BigDecimal(feeForTx)));
        String formattedTotalBTC = BRCurrency.getFormattedCurrencyString(ctx, "LTC", BRExchange.getBitcoinForSatoshis(ctx, new BigDecimal(total)));

        String formattedAmount = BRCurrency.getFormattedCurrencyString(ctx, iso, BRExchange.getAmountFromSatoshis(ctx, iso, new BigDecimal(request.amount)));
        String formattedFee = BRCurrency.getFormattedCurrencyString(ctx, iso, BRExchange.getAmountFromSatoshis(ctx, iso, new BigDecimal(feeForTx)));
        String formattedTotal = BRCurrency.getFormattedCurrencyString(ctx, iso, BRExchange.getAmountFromSatoshis(ctx, iso, new BigDecimal(total)));

        //formatted text
        return receiver + "\n\n"
                + "Amount to Send:" + " " + formattedAmountBTC + " (" + formattedAmount + ")"
                + "\n"  + "Network Fee:" + " " + formattedFeeBTC + " (" + formattedFee + ")"
                + "\n"  + "Total Cost:" + " "  + formattedTotalBTC + " (" + formattedTotal + ")"
                + (request.comment == null ? "" : "\n\n" + request.comment);
    }

    public String getReceiver(PaymentItem item) {
        String receiver;
        boolean certified = false;
        if (item.cn != null && item.cn.length() != 0) {
            certified = true;
        }
        StringBuilder allAddresses = new StringBuilder();
        for (String s : item.addresses) {
            allAddresses.append(s + ", ");
        }
        receiver = allAddresses.toString();
        allAddresses.delete(allAddresses.length() - 2, allAddresses.length());
        if (certified) {
            receiver = "certified: " + item.cn + "\n";
        }
        return receiver;
    }

    public boolean isSmallerThanMin(Context app, PaymentItem paymentRequest) {
        long minAmount = BRWalletManager.getInstance().getMinOutputAmountRequested();
        return paymentRequest.amount < minAmount;
    }

    public boolean isLargerThanBalance(Context app, PaymentItem paymentRequest) {
        return paymentRequest.amount > BRWalletManager.getInstance().getBalance(app) && paymentRequest.amount > 0;
    }

    public boolean notEnoughForFee(Context app, PaymentItem paymentRequest) {
        BRWalletManager m = BRWalletManager.getInstance();
        long feeForTx = m.feeForTransaction(paymentRequest.addresses[0], paymentRequest.amount);
        if (feeForTx == 0) {
            feeForTx = m.feeForTransaction(paymentRequest.addresses[0], m.getMaxOutputAmount());
            return feeForTx != 0;
        }
        return false;
    }

    public static void showSpendNotAllowed(final Context app) {
        Log.d(TAG, "showSpendNotAllowed");
        ((Activity) app).runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private class InsufficientFundsException extends Exception {

        public InsufficientFundsException(long amount, long balance) {
            super("Balance: " + balance + " satoshis, amount: " + amount + " satoshis.");
        }

    }

    private class AmountSmallerThanMinException extends Exception {

        public AmountSmallerThanMinException(long amount, long balance) {
            super("Balance: " + balance + " satoshis, amount: " + amount + " satoshis.");
        }

    }

    private class SpendingNotAllowed extends Exception {

        public SpendingNotAllowed() {
            super("spending is not allowed at the moment");
        }

    }

    private class FeeNeedsAdjust extends Exception {

        public FeeNeedsAdjust(long amount, long balance, long fee) {
            super("Balance: " + balance + " satoshis, amount: " + amount + " satoshis, fee: " + fee + " satoshis.");
        }

    }
}
