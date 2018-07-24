package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities.util.BRActivity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRSharedPrefs;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.TransactionDataSource;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRPeerManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRWalletManager;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 8/4/15.
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

public class BreadActivity extends BRActivity implements BRWalletManager.OnBalanceChanged,
        BRPeerManager.OnTxStatusUpdate, BRSharedPrefs.OnIsoChangedListener,
        TransactionDataSource.OnTxAddedListener{

    private static final String TAG = BreadActivity.class.getName();


    private static BreadActivity app;

    public static BreadActivity getApp() {
        return app;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //leave it empty, avoiding the os bug
    }

    private void setUrlHandler(Intent intent) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void setListeners() {

    }

    private void swap() {
    }

    private void setPriceTags(boolean btcPreferred, boolean animate) {
    }

    private void setUpBarFlipper() {
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setupNetworking() {

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initializeViews() {

    }

    private void saveVisibleFragment() {
    }



    @Override
    public void onBalanceChanged(final long balance) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void updateUI() {
    }

    @Override
    public void onStatusUpdate() {

    }

    @Override
    public void onIsoChanged(String iso) {
        updateUI();
    }

    @Override
    public void onTxAdded() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }


}