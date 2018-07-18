package module.nrlwallet.com.nrlwalletsdk.Coins;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Account;
import module.nrlwallet.com.nrlwalletsdk.Stellar.AssetTypeNative;
import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.PaymentOperation;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NRLStellar extends NRLCoin {
    String url_server = "https://xlm.mousebelt.com/api/v1";
    Network network = Neo.MAIN_NET;
    int coinType = 148;
    String seedKey = "ed25519 seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bseed;
    String sseed;
    String privateKey = "";
    String walletAddress;
    KeyPair keyPair;
    Account account;
    String balance = "0";
    private long sequenceNumber;
    JSONArray operations = new JSONArray();

    public NRLStellar(byte[] bseed, String seed) {
        super(bseed, Neo.MAIN_NET, 148, "ed25519 seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        this.bseed = bseed;
        this.sseed = seed;
        this.init();
    }

    private void init() {
        byte[] tmpseed = Arrays.copyOfRange(bseed, 32, 64);
        keyPair = KeyPair.fromSecretSeed(tmpseed);
        walletAddress = keyPair.getAccountId();
        createWallet();
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return this.walletAddress;
    }

    public void getBalance(NRLCallback callback) {
        this.checkBalance(callback);
    }

    public void createWallet() {
        String url_getbalance = url_server + "/account/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody results = response.body();
                    try {
                        JSONObject object = new JSONObject(results.string());
                        JSONObject data = object.getJSONObject("data");

                        sequenceNumber = Long.parseLong(data.getString("sequence"));
//                        account = new Account(keyPair, sequenceNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
                    callback.onResponse("Failed");
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
                                if(object.getString("type").equals("payment")){
                                    returnVal.put("txid", object.getString("transaction_hash"));
                                    if(object.getString("from").equals(walletAddress)){
                                        returnVal.put("value", "-" + object.getString("amount"));
                                    }else {
                                        returnVal.put("value", "+" + object.getString("amount"));
                                    }
                                    arrTransactions.put(returnVal);
                                } else if(object.getString("type").equals("create_account")) {

                                }


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
    public void SendTransaction(long amount, String destinationAddress,NRLCallback callback) {
        String url_getbalance = url_server + "/account/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody results = response.body();
                    try {
                        JSONObject object = new JSONObject(results.string());
                        JSONObject data = object.getJSONObject("data");
                        sequenceNumber = Long.parseLong(data.getString("sequence"));
                        createTransaction(amount, destinationAddress, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                }
            }
        });
    }

    public void createTransaction(long amount, String destinationAddress, NRLCallback callback) {
        module.nrlwallet.com.nrlwalletsdk.Stellar.Network.usePublicNetwork();
        KeyPair destination = KeyPair.fromAccountId(destinationAddress);

        byte[] tmpseed = Arrays.copyOfRange(bseed, 32, 64);
        KeyPair source = KeyPair.fromSecretSeed(tmpseed);
        account = new Account(source, sequenceNumber);

        PaymentOperation operation = new PaymentOperation.Builder(destination, new AssetTypeNative(), amount).build();

        Transaction transaction = new Transaction.Builder(this.account)
                .addOperation(operation)
                .build();

        transaction.sign(source);


        System.out.println(transaction.toEnvelopeXdrBase64());
        String url_getTransaction = url_server + "/transaction";
        String json = "{\"tx\":\"" + transaction.toEnvelopeXdrBase64() + "\"}";
        RequestBody body = RequestBody.create(HTTPRequest.JSON, json);


        new HTTPRequest().run(url_getTransaction, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("************----------- response : " + response.body().string());
                    callback.onResponse("Success!");
                }else{
                    callback.onResponse("Failed");
                }
            }
        });
    }
}
