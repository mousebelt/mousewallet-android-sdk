package module.nrlwallet.com.nrlwalletsdk.abstracts;

import org.json.JSONArray;

public interface NRLCallback {

    void onFailure(Throwable t);

    void onResponse(String response);

    void onResponseArray(JSONArray jsonArray);

}
