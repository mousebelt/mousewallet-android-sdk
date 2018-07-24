package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.NetworkOnMainThreadException;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.WorkerThread;
import android.util.Log;

import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.BreadApp;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities.util.ActivityUTILS;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.PaymentItem;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.PaymentRequestWrapper;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRReportsManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRSharedPrefs;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.PaymentProtocolPostPaymentTask;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRConstants;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.TypesConverter;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.Utils;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRWalletManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.APIClient;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.entities.TxMetaData;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.tools.BRBitId;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.tools.KVStoreManager;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 4/14/16.
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

public class PostAuth {
    public static final String TAG = PostAuth.class.getName();

    private String phraseForKeyStore;
    public PaymentItem paymentItem;
    private PaymentRequestWrapper paymentRequest;

    private static PostAuth instance;

    private PostAuth() {
    }

    public static PostAuth getInstance() {
        if (instance == null) {
            instance = new PostAuth();
        }
        return instance;
    }

    public void onCreateWalletAuth(Activity app, boolean authAsked) {
    }

    public void onPhraseCheckAuth(Activity app, boolean authAsked) {
    }

    public void onPhraseProveAuth(Activity app, boolean authAsked) {
    }

    public void onBitIDAuth(Activity app, boolean authenticated) {
    }

    public void onRecoverWalletAuth(Activity app, boolean authAsked) {

    }

    public void onPublishTxAuth(final Context app, boolean authAsked) {
    }

    public void onSendBch(final Activity app, boolean authAsked, String bchAddress) {

    }

    public void onPaymentProtocolRequest(Activity app, boolean authAsked) {
    }

    public void setPhraseForKeyStore(String phraseForKeyStore) {
        this.phraseForKeyStore = phraseForKeyStore;
    }


    public void setPaymentItem(PaymentItem item) {
        this.paymentItem = item;
    }

    public void setTmpPaymentRequest(PaymentRequestWrapper paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public void onCanaryCheck(final Activity app, boolean authAsked) {
        BRWalletManager.getInstance().startTheWalletIfExists(app);
    }

}
