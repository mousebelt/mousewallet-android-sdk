package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities.util.ActivityUTILS;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.interfaces.BRAuthCompletion;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRSharedPrefs;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.Utils;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRWalletManager;

import java.util.concurrent.TimeUnit;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 8/20/15.
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

public class AuthManager {
    public static final String TAG = AuthManager.class.getName();
    private static AuthManager instance;
    private String previousTry;

    private AuthManager() {
        previousTry = "";
    }

    public static AuthManager getInstance() {
        if (instance == null)
            instance = new AuthManager();
        return instance;
    }

    public boolean checkAuth(CharSequence passSequence, Context context) {
        Log.e(TAG, "checkAuth: ");
        String tempPass = passSequence.toString();
        if (!previousTry.equals(tempPass)) {
            int failCount = BRKeyStore.getFailCount(context);
            BRKeyStore.putFailCount(failCount + 1, context);
        }
        previousTry = tempPass;

        String pass = BRKeyStore.getPinCode(context);
        boolean match = pass != null && tempPass.equals(pass);
        if (!match) {
            if (BRKeyStore.getFailCount(context) >= 3) {
                setWalletDisabled((Activity) context);
            }
        }

        return match;
    }

    //when pin auth success
    public void authSuccess(final Context app) {
        //put the new total limit in 3 seconds, leave some time for the core to register any new tx
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AuthManager.getInstance().setTotalLimit(app, BRWalletManager.getInstance().getTotalSent()
                        + BRKeyStore.getSpendLimit(app));
            }
        });

        BRKeyStore.putFailCount(0, app);
        BRKeyStore.putLastPinUsedTime(System.currentTimeMillis(), app);
    }

    public void authFail(Context app) {

    }

    public boolean isWalletDisabled(Activity app) {
        int failCount = BRKeyStore.getFailCount(app);
        long secureTime = BRSharedPrefs.getSecureTime(app);
        long failTimestamp = BRKeyStore.getFailTimeStamp(app);
        return failCount >= 3 && secureTime < failTimestamp + Math.pow(6, failCount - 3) * 60.0;

    }

    public void setWalletDisabled(Activity app) {
        int failCount = BRKeyStore.getFailCount(app);
        long now = System.currentTimeMillis() / 1000;
        long secureTime = BRSharedPrefs.getSecureTime(app);
        long failTimestamp = BRKeyStore.getFailTimeStamp(app);
        double waitTimeMinutes = (failTimestamp + Math.pow(6, failCount - 3) * 60.0 - secureTime) / 60.0;

    }

    public void setPinCode(String pass, Activity context) {
        BRKeyStore.putFailCount(0, context);
        BRKeyStore.putPinCode(pass, context);
        BRKeyStore.putLastPinUsedTime(System.currentTimeMillis(), context);
        setSpendingLimitIfNotSet(context);
    }

    /**
     * Returns the total current limit that cannot be surpass without a pin
     */
    public long getTotalLimit(Context activity) {
        return BRKeyStore.getTotalLimit(activity);
    }

    /**
     * Sets the total current limit that cannot be surpass without a pin
     */
    public void setTotalLimit(Context activity, long limit) {
        BRKeyStore.putTotalLimit(limit, activity);
    }

    private void setSpendingLimitIfNotSet(final Activity activity) {
        if (activity == null) return;
        long limit = AuthManager.getInstance().getTotalLimit(activity);
        if (limit == 0) {
            BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                @Override
                public void run() {
                    long totalSpent = BRWalletManager.getInstance().getTotalSent();
                    long totalLimit = totalSpent + BRKeyStore.getSpendLimit(activity);
                    setTotalLimit(activity, totalLimit);
                }
            });

        }
    }

    public void updateDots(Context context, int pinLimit, String pin, View dot1, View dot2, View dot3, View dot4, View dot5, View dot6, int emptyPinRes, final OnPinSuccess onPinSuccess) {
        if (dot1 == null || context == null) return;
        int selectedDots = pin.length();

        if (pinLimit == 6) {
            dot6.setVisibility(View.VISIBLE);
            dot1.setVisibility(View.VISIBLE);
            selectedDots--;
        } else {
            dot6.setVisibility(View.GONE);
            dot1.setVisibility(View.GONE);
        }


        if (pin.length() == pinLimit) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onPinSuccess.onSuccess();

                }
            }, 100);

        }
    }

    public void authPrompt(final Context context, String title, String message, boolean forcePin, BRAuthCompletion completion) {
        if (context == null || !(context instanceof Activity)) {
            Log.e(TAG, "authPrompt: context is null or not Activity: " + context);
            return;
        }
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Activity.KEYGUARD_SERVICE);


        if (BRKeyStore.getFailCount(context) != 0) {
        }
        long passTime = BRKeyStore.getLastPinUsedTime(context);

        if (passTime + TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS) <= System.currentTimeMillis()) {
        }


        final Activity app = (Activity) context;

    }

    public interface OnPinSuccess {
        void onSuccess();
    }
}
