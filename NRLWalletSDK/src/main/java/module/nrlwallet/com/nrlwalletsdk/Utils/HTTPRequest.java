package module.nrlwallet.com.nrlwalletsdk.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HTTPRequest {
    OkHttpClient client = new OkHttpClient();
    String urlServer = "http://54.152.5.218/api/v1";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Call run(String url_path, Callback callback) {
        String url = urlServer + url_path;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
