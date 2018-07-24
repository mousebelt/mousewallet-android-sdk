package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.view.View;

import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities.BreadActivity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.TxItem;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRPeerManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRWalletManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 7/19/17.
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
public class TxManager {

    private static final String TAG = TxManager.class.getName();
    private static TxManager instance;
    public PromptManager.PromptItem currentPrompt;
    public PromptManager.PromptInfo promptInfo;

    public static TxManager getInstance() {
        if (instance == null) instance = new TxManager();
        return instance;
    }

    public void init(final BreadActivity app) {
    }

    private TxManager() {
    }

    public void onResume(final Activity app) {
        crashIfNotMain();
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                final double progress = BRPeerManager.syncProgress(BRSharedPrefs.getStartHeight(app));
                BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (progress > 0 && progress < 1) {
                            currentPrompt = PromptManager.PromptItem.SYNCING;
                            updateCard(app);
                        } else {
                            showNextPrompt(app);
                        }
                    }
                });
            }
        });

    }

    public void showPrompt(Activity app, PromptManager.PromptItem item) {
        crashIfNotMain();
        if (item == null) throw new RuntimeException("can't be null");
//        BREventManager.getInstance().pushEvent("prompt." + PromptManager.getInstance().getPromptName(item) + ".displayed");
        if (currentPrompt != PromptManager.PromptItem.SYNCING) {
            currentPrompt = item;
        }
        updateCard(app);
    }

    public void hidePrompt(final Activity app, final PromptManager.PromptItem item) {
        crashIfNotMain();
        if (currentPrompt == PromptManager.PromptItem.SHARE_DATA) {
            BRSharedPrefs.putShareDataDismissed(app, true);
        }
        currentPrompt = null;
        if (item == PromptManager.PromptItem.SYNCING) {
            showNextPrompt(app);
            updateCard(app);
        } else {

        }

    }

    private void showNextPrompt(Activity app) {
        crashIfNotMain();
    }

    @WorkerThread
    public synchronized void updateTxList(final Context app) {
        long start = System.currentTimeMillis();
        final TxItem[] arr = BRWalletManager.getInstance().getTransactions();
        final List<TxItem> items = arr == null ? null : new LinkedList<>(Arrays.asList(arr));

        long took = (System.currentTimeMillis() - start);
        if (took > 500)
            Log.e(TAG, "updateTxList: took: " + took);

    }

    public void updateCard(final Context app) {
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                updateTxList(app);
            }
        });
    }


    private void crashIfNotMain() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalAccessError("Can only call from main thread");
        }
    }

}
