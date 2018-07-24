package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities;

import android.os.Bundle;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities.util.BRActivity;
public class InputWordsActivity extends BRActivity {
    private static final String TAG = InputWordsActivity.class.getName();
    public static boolean appVisible = false;
    private static InputWordsActivity app;

    public static InputWordsActivity getApp() {
        return app;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void clearWords() {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }


}
