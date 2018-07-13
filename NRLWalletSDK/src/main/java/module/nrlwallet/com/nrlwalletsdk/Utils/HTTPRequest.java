package module.nrlwallet.com.nrlwalletsdk.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPRequest {
    OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Call run(String url_path, Callback callback) {
        String url = url_path;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(360, TimeUnit.SECONDS);
        builder.readTimeout(360, TimeUnit.SECONDS);
        builder.writeTimeout(360, TimeUnit.SECONDS);
        client = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call run(String url_path, RequestBody formBody, Callback callback) {
        String url = url_path;

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(360, TimeUnit.SECONDS);
        builder.readTimeout(360, TimeUnit.SECONDS);
        builder.writeTimeout(360, TimeUnit.SECONDS);
        client = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
