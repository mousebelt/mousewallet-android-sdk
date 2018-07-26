package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.RequiresApi;
import android.support.annotation.WorkerThread;
import android.util.Log;

import module.nrlwallet.com.nrlwalletsdk.Coins.NRLLite;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.BreadApp;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities.util.ActivityUTILS;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.BRMerkleBlockEntity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.BRPeerEntity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.BRTransactionEntity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.ImportPrivKeyEntity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.TxItem;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BREventManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRReportsManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRSharedPrefs;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security.BRKeyStore;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.MerkleBlockDataSource;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.PeerDataSource;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.TransactionDataSource;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.ImportPrivKeyTask;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRConstants;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRCurrency;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRExchange;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.TypesConverter;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.Utils;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.Bip39Reader;

import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.entities.WalletInfo;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.tools.KVStoreManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 12/10/15.
 * Copyright (c) 2016 breadwallet LLC
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

public class BRWalletManager {
    private static final String TAG = BRWalletManager.class.getName();

    private static BRWalletManager instance;
    public List<OnBalanceChanged> balanceListeners;

    public void setBalance(final Context context, long balance) {
        if (context == null) {
            Log.e(TAG, "setBalance: FAILED TO SET THE BALANCE");
            return;
        }
        BRSharedPrefs.putCatchedBalance(context, balance);
        refreshAddress(context);

        for (OnBalanceChanged listener : balanceListeners) {
            if (listener != null) listener.onBalanceChanged(balance);
        }
    }

    public void refreshBalance(Activity app) {
        long nativeBalance = nativeBalance();
        if (nativeBalance != -1) {
            setBalance(app, nativeBalance);
        } else {
            Log.e(TAG, "UpdateUI, nativeBalance is -1 meaning _wallet was null!");
        }
    }

    public long getBalance(Context context) {
        return BRSharedPrefs.getCatchedBalance(context);
    }

    private BRWalletManager() {
        balanceListeners = new ArrayList<>();
    }

    public static BRWalletManager getInstance() {
        if (instance == null) {
            instance = new BRWalletManager();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean generateRandomSeed(final Context ctx) {
        SecureRandom sr = new SecureRandom();
        final String[] words;
        List<String> list;
        String languageCode = Locale.getDefault().getLanguage();
        list = Bip39Reader.bip39List(ctx, languageCode);
        words = list.toArray(new String[list.size()]);
        final byte[] randomSeed = sr.generateSeed(16);
        if (words.length < 2000) {
            BRReportsManager.reportBug(new IllegalArgumentException("the list is wrong, size: " + words.length), true);
        }
        if (randomSeed.length == 0) throw new NullPointerException("failed to create the seed");
        byte[] strPhrase = encodeSeed(randomSeed, words);
        if (strPhrase == null || strPhrase.length == 0) {
            BRReportsManager.reportBug(new NullPointerException("failed to encodeSeed"), true);
        }
        boolean success = false;
        try {
            success = BRKeyStore.putPhrase(strPhrase, ctx, BRConstants.PUT_PHRASE_NEW_WALLET_REQUEST_CODE);
        } catch (UserNotAuthenticatedException e) {
            return false;
        }
        if (!success) return false;
        byte[] phrase;
        try {
            phrase = BRKeyStore.getPhrase(ctx, 0);
        } catch (UserNotAuthenticatedException e) {
            throw new RuntimeException("Failed to retrieve the phrase even though at this point the system auth was asked for sure.");
        }
        if (Utils.isNullOrEmpty(phrase)) throw new NullPointerException("phrase is null!!");
        byte[] nulTermPhrase = TypesConverter.getNullTerminatedPhrase(phrase);
        if (nulTermPhrase == null || nulTermPhrase.length == 0)
            throw new RuntimeException("nulTermPhrase is null");
        byte[] seed = getSeedFromPhrase(nulTermPhrase);
        if (seed == null || seed.length == 0) throw new RuntimeException("seed is null");
        byte[] authKey = getAuthPrivKeyForAPI(seed);
        if (authKey == null || authKey.length == 0) {
            BRReportsManager.reportBug(new IllegalArgumentException("authKey is invalid"), true);
        }
        BRKeyStore.putAuthKey(authKey, ctx);
        int walletCreationTime = (int) (System.currentTimeMillis() / 1000);
        BRKeyStore.putWalletCreationTime(walletCreationTime, ctx);
        final WalletInfo info = new WalletInfo();
        info.creationDate = walletCreationTime;
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                KVStoreManager.getInstance().putWalletInfo(ctx, info); //push the creation time to the kv store
            }
        });

        byte[] strBytes = TypesConverter.getNullTerminatedPhrase(strPhrase);
        byte[] pubKey = BRWalletManager.getInstance().getMasterPubKey(strBytes);
        BRKeyStore.putMasterPublicKey(pubKey, ctx);

        return true;

    }

    public boolean wipeKeyStore(Context context) {
        Log.d(TAG, "wipeKeyStore");
        return BRKeyStore.resetWalletKeyStore(context);
    }

    /**
     * true if keystore is available and we know that no wallet exists on it
     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean noWallet(Context ctx) {
        byte[] pubkey = BRKeyStore.getMasterPublicKey(ctx);

        if (pubkey == null || pubkey.length == 0) {
            byte[] phrase;
            try {
                phrase = BRKeyStore.getPhrase(ctx, 0);
                //if not authenticated, an error will be thrown and returned false, so no worry about mistakenly removing the wallet
                if (phrase == null || phrase.length == 0) {
                    return true;
                }
            } catch (UserNotAuthenticatedException e) {
                return false;
            }

        }
        return false;
    }

    public boolean noWalletForPlatform(Context ctx) {
        byte[] pubkey = BRKeyStore.getMasterPublicKey(ctx);
        return pubkey == null || pubkey.length == 0;
    }

    public static boolean refreshAddress(Context ctx) {
        String address = getReceiveAddress();
        if (Utils.isNullOrEmpty(address)) {
            Log.e(TAG, "refreshAddress: WARNING, retrieved address:" + address);
            return false;
        }
        BRSharedPrefs.putReceiveAddress(ctx, address);
        return true;

    }

    public void wipeWalletButKeystore(final Context ctx) {
        Log.d(TAG, "wipeWalletButKeystore");
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                BRPeerManager.getInstance().peerManagerFreeEverything();
                walletFreeEverything();
                TransactionDataSource.getInstance(ctx).deleteAllTransactions();
                MerkleBlockDataSource.getInstance(ctx).deleteAllBlocks();
                PeerDataSource.getInstance(ctx).deleteAllPeers();
                BRSharedPrefs.clearAllPrefs(ctx);
            }
        });

    }

    public boolean isNetworkAvailable(Context ctx) {
        if (ctx == null) return false;
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
    public boolean confirmSweep(final Context ctx, final String privKey) {
        if (ctx == null) return false;
        if (isValidBitcoinBIP38Key(privKey)) {
            Log.d(TAG, "isValidBitcoinBIP38Key true");
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            return true;
        } else if (isValidBitcoinPrivateKey(privKey)) {
            Log.d(TAG, "isValidBitcoinPrivateKey true");
            new ImportPrivKeyTask(((Activity) ctx)).execute(privKey);
            return true;
        } else {
            Log.e(TAG, "confirmSweep: !isValidBitcoinPrivateKey && !isValidBitcoinBIP38Key");
            return false;
        }
    }


    public static void onBalanceChanged(final long balance) {
        Log.d(TAG, "onBalanceChanged:  " + balance);
        Context app = NRLLite.getBreadContext();
        BRWalletManager.getInstance().setBalance(app, balance);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void onTxAdded(byte[] tx, int blockHeight, long timestamp, final long amount, String hash) {
        Log.d(TAG, "onTxAdded: " + String.format("tx.length: %d, blockHeight: %d, timestamp: %d, amount: %d, hash: %s", tx.length, blockHeight, timestamp, amount, hash));

        final Context ctx = NRLLite.getBreadContext();
        if (amount > 0) {
            BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
                @Override
                public void run() {
                    String am = BRCurrency.getFormattedCurrencyString(ctx, "LTC", BRExchange.getBitcoinForSatoshis(ctx, new BigDecimal(amount)));
                    String amCur = BRCurrency.getFormattedCurrencyString(ctx, BRSharedPrefs.getIso(ctx), BRExchange.getAmountFromSatoshis(ctx, BRSharedPrefs.getIso(ctx), new BigDecimal(amount)));
                    String formatted = String.format("%s (%s)", am, amCur);
                }
            });
        }
        if (ctx != null)
            TransactionDataSource.getInstance(ctx).putTransaction(new BRTransactionEntity(tx, blockHeight, timestamp, hash));
        else
            Log.e(TAG, "onTxAdded: ctx is null!");
    }


    public static void onTxUpdated(String hash, int blockHeight, int timeStamp) {
        Log.d(TAG, "onTxUpdated: " + String.format("hash: %s, blockHeight: %d, timestamp: %d", hash, blockHeight, timeStamp));
        Context ctx = NRLLite.getBreadContext();
        if (ctx != null) {
            TransactionDataSource.getInstance(ctx).updateTxBlockHeight(hash, blockHeight, timeStamp);

        } else {
            Log.e(TAG, "onTxUpdated: Failed, ctx is null");
        }
    }

    public static void onTxDeleted(String hash, int notifyUser, final int recommendRescan) {
        Log.e(TAG, "onTxDeleted: " + String.format("hash: %s, notifyUser: %d, recommendRescan: %d", hash, notifyUser, recommendRescan));
        final Context ctx = NRLLite.getBreadContext();
        if (ctx != null) {
            BRSharedPrefs.putScanRecommended(ctx, true);
        } else {
            Log.e(TAG, "onTxDeleted: Failed! ctx is null");
        }
    }


    public void startTheWalletIfExists(final Activity app) {
        final BRWalletManager m = BRWalletManager.getInstance();
    }

    @WorkerThread
    public void initWallet(final Context ctx) {
//        if (ActivityUTILS.isMainThread()) throw new NetworkOnMainThreadException();
        Log.d(TAG, "initWallet:" + Thread.currentThread().getName());
        if (ctx == null) {
            Log.e(TAG, "initWallet: ctx is null");
            return;
        }
        BRWalletManager m = BRWalletManager.getInstance();
        final BRPeerManager pm = BRPeerManager.getInstance();

        if (!m.isCreated()) {
            List<BRTransactionEntity> transactions = TransactionDataSource.getInstance(ctx).getAllTransactions();
            int transactionsCount = transactions.size();
            if (transactionsCount > 0) {
                m.createTxArrayWithCount(transactionsCount);
                for (BRTransactionEntity entity : transactions) {
                    m.putTransaction(entity.getBuff(), entity.getBlockheight(), entity.getTimestamp());
                }
            }

            byte[] pubkeyEncoded = BRKeyStore.getMasterPublicKey(ctx);
            if (Utils.isNullOrEmpty(pubkeyEncoded)) {
                Log.e(TAG, "initWallet: pubkey is missing");
                return;
            }
            //Save the first address for future check
            m.createWallet(transactionsCount, pubkeyEncoded);
            String firstAddress = BRWalletManager.getFirstAddress(pubkeyEncoded);
            BRSharedPrefs.putFirstAddress(ctx, firstAddress);
            long fee = BRSharedPrefs.getFeePerKb(ctx);
            if (fee == 0) {
                fee = BRConstants.DEFAULT_FEE_PER_KB;
                BREventManager.getInstance().pushEvent("wallet.didUseDefaultFeePerKB");
            }
//            BRWalletManager.getInstance().setFeePerKb(fee, isEconomyFee);
        }

        if (!pm.isCreated()) {
            List<BRMerkleBlockEntity> blocks = MerkleBlockDataSource.getInstance(ctx).getAllMerkleBlocks();
            List<BRPeerEntity> peers = PeerDataSource.getInstance(ctx).getAllPeers();
            final int blocksCount = blocks.size();
            final int peersCount = peers.size();
            if (blocksCount > 0) {
                pm.createBlockArrayWithCount(blocksCount);
                for (BRMerkleBlockEntity entity : blocks) {
                    pm.putBlock(entity.getBuff(), entity.getBlockHeight());
                }
            }
            if (peersCount > 0) {
                pm.createPeerArrayWithCount(peersCount);
                for (BRPeerEntity entity : peers) {
                    pm.putPeer(entity.getAddress(), entity.getPort(), entity.getTimeStamp());
                }
            }
            Log.d(TAG, "blocksCount before connecting: " + blocksCount);
            Log.d(TAG, "peersCount before connecting: " + peersCount);

            int walletTime = 1532000000;//BRKeyStore.getWalletCreationTime(ctx);

            Log.e(TAG, "initWallet: walletTime: " + walletTime);
            pm.create(walletTime, blocksCount, peersCount);

        }
        BRPeerManager.getInstance().updateFixedPeer(ctx);
        pm.connect();
        if (BRSharedPrefs.getStartHeight(ctx) == 0)
            BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                @Override
                public void run() {
                    BRSharedPrefs.putStartHeight(ctx, BRPeerManager.getCurrentBlockHeight());
                }
            });
    }

    public void addBalanceChangedListener(OnBalanceChanged listener) {
        if (balanceListeners == null) {
            Log.e(TAG, "addBalanceChangedListener: statusUpdateListeners is null");
            return;
        }
        if (!balanceListeners.contains(listener))
            balanceListeners.add(listener);
    }

    public void removeListener(OnBalanceChanged listener) {
        if (balanceListeners == null) {
            Log.e(TAG, "addBalanceChangedListener: statusUpdateListeners is null");
            return;
        }
        balanceListeners.remove(listener);

    }

    public interface OnBalanceChanged {
        void onBalanceChanged(long balance);

    }

    private native byte[] encodeSeed(byte[] seed, String[] wordList);

    public native void createWallet(int transactionCount, byte[] pubkey);

    public native void putTransaction(byte[] transaction, long blockHeight, long timeStamp);

    public native void createTxArrayWithCount(int count);

    public native byte[] getMasterPubKey(byte[] normalizedString);

    public static native String getReceiveAddress();

    public native TxItem[] getTransactions();

    public static native boolean validateAddress(String address);

    public native boolean addressContainedInWallet(String address);

    public native boolean addressIsUsed(String address);

    public native int feeForTransaction(String addressHolder, long amountHolder);

    public native int feeForTransactionAmount(long amountHolder);

    public native long getMinOutputAmount();

    public native long getMaxOutputAmount();

    public native boolean isCreated();

    public native byte[] tryTransaction(String addressHolder, long amountHolder);

    // returns the given amount (amount is in satoshis) in local currency units (i.e. pennies, pence)
    // price is local currency units per bitcoin
    public native long localAmount(long amount, double price);

    // returns the given local currency amount in satoshis
    // price is local currency units (i.e. pennies, pence) per bitcoin
    public native long bitcoinAmount(long localAmount, double price);

    public native void walletFreeEverything();

    public native boolean validateRecoveryPhrase(String[] words, String phrase);

    public native static String getFirstAddress(byte[] mpk);

    public native byte[] publishSerializedTransaction(byte[] serializedTransaction, byte[] phrase);

    public native long getTotalSent();

    public native long setFeePerKb(long fee, boolean ignore);

    public native boolean isValidBitcoinPrivateKey(String key);

    public native boolean isValidBitcoinBIP38Key(String key);

    public native String getAddressFromPrivKey(String key);

    public native void createInputArray();

    public native void addInputToPrivKeyTx(byte[] hash, int vout, byte[] script, long amount);

    public native boolean confirmKeySweep(byte[] tx, String key);

    public native ImportPrivKeyEntity getPrivKeyObject();

    public native String decryptBip38Key(String privKey, String pass);

    public native String reverseTxHash(String txHash);

    public native String txHashToHex(byte[] txHash);

//    public native String txHashSha256Hex(String txHash);

    public native long nativeBalance();

    public native int getTxCount();

    public native long getMinOutputAmountRequested();

    public static native byte[] getAuthPrivKeyForAPI(byte[] seed);

    public static native String getAuthPublicKeyForAPI(byte[] privKey);

    public static native byte[] getSeedFromPhrase(byte[] phrase);

    public static native boolean isTestNet();

    public static native byte[] sweepBCash(byte[] pubKey, String address, byte[] phrase);

    public static native long getBCashBalance(byte[] pubKey);

    public static native int getTxSize(byte[] serializedTx);

}
