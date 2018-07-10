package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Account;
import module.nrlwallet.com.nrlwalletsdk.Stellar.AssetTypeNative;
import module.nrlwallet.com.nrlwalletsdk.Stellar.CreateAccountOperation;
import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Memo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.PaymentOperation;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Util;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class NRLStellar extends NRLCoin {
    String url_server = "https://xlm.mousebelt.com/api/v1";
    Network network = Neo.MAIN_NET;
    int coinType = 148;
    String seedKey = "ed25519 seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bseed;
    String privateKey = "";
    String walletAddress;
    String Wif;
    KeyPair keyPair;
    Account account;
    String balance = "0";
    JSONArray transactions = new JSONArray();
    JSONArray operations = new JSONArray();
    OkHttpClient client = new OkHttpClient();

    public NRLStellar(byte[] bseed) {
        super(bseed, Neo.MAIN_NET, 148, "ed25519 seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        this.bseed = bseed;
        this.init();
    }

    private void init() {
        byte[] tmpseed = Arrays.copyOfRange(bseed, 0, 32);
        keyPair = KeyPair.fromSecretSeed(tmpseed);
        account = new Account(keyPair, 2908908335136768L);
        walletAddress = keyPair.getAccountId();

    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return walletAddress;
    }

    public void getBalance(NRLCallback callback) {
        this.checkBalance(callback);
    }

    private void checkBalance(NRLCallback callback) {
        String url_getbalance = url_server + "/balance/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result =   (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            JSONArray data = jsonObj.getJSONArray("data");
                            for(int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                String ticker = obj.getString("asset_type");
                                if(ticker.equals("native")){
                                    balance = obj.getString("balance");
                                    callback.onResponse(balance);
                                    return;
                                }
                            }
                        } else {
                            callback.onResponse(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    private void checkTransactions1(NRLCallback callback) {
        String url_getTransaction = url_server + "/address/txs/" + this.walletAddress;
        new HTTPRequest().run(url_getTransaction, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body =   (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            transactions = data.getJSONArray("result");
                            callback.onResponseArray(transactions);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    public void getTransactionsJson(NRLCallback callback) {
        String url_getTransaction = url_server + "/address/payments/" + this.walletAddress;

        new HTTPRequest().run(url_getTransaction, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body =   (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            operations = data.getJSONArray("result");
                            callback.onResponseArray(operations);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    public void getTransactions(NRLCallback callback) {
        String url_getTransaction = url_server + "/address/payments/" + this.walletAddress;

        new HTTPRequest().run(url_getTransaction, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body =   (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            operations = data.getJSONArray("result");
                            JSONArray arrTransactions = new JSONArray();
                            for(int i = 0; i < operations.length(); i++) {
                                JSONObject returnVal = new JSONObject();
                                JSONObject object = operations.getJSONObject(i);
                                returnVal.put("txid", object.getString("transaction_hash"));
                                if(object.getString("from").equals(walletAddress)){
                                    returnVal.put("value", "-" + object.getString("amount"));
                                }else {
                                    returnVal.put("value", "+" + object.getString("amount"));
                                }
                                arrTransactions.put(returnVal);
                            }
                            callback.onResponseArray(arrTransactions);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    public void createTransaction(long amount, String destinationAddress, NRLCallback callback) {
        module.nrlwallet.com.nrlwalletsdk.Stellar.Network.usePublicNetwork();
        KeyPair destination = KeyPair.fromAccountId(destinationAddress);
        byte[] tmpseed = Arrays.copyOfRange(bseed, 0, 32);
        KeyPair source = KeyPair.fromSecretSeed(tmpseed);
        Account sourceAccount = new Account(source, 2908908335136768L);//3009998980382720L);
        PaymentOperation operation = new PaymentOperation.Builder(destination, new AssetTypeNative(), amount).build();

        Transaction transaction = new Transaction.Builder(sourceAccount)
                .addOperation(operation)
                .build();

        transaction.sign(keyPair);

        System.out.println(transaction.toEnvelopeXdrBase64());
        String url_getTransaction = url_server + "/transaction";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tx", transaction.toEnvelopeXdrBase64())
                .build();

        new HTTPRequest().run(url_getTransaction, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.toString());
            }
        });
    }
}
