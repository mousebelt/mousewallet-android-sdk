package module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities;

import android.os.Bundle;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.activities.util.BRActivity;


public class DisabledActivity extends BRActivity {
    private static final String TAG = DisabledActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void refresh() {
    }

    @Override
    protected void onStop() {
        super.onStop();
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }
}
