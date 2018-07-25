package module.nrlwallet.com.nrlwalletsdk.abstracts;

import org.json.JSONObject;

public interface LTCCallback {
    void onResponse(JSONObject response);
    void onFailed(String response);
}
