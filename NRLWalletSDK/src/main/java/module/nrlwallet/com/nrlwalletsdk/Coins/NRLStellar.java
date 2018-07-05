package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

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
import module.nrlwallet.com.nrlwalletsdk.Stellar.Memo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.PaymentOperation;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import neoutils.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class NRLStellar extends NRLCoin {
    String url_server = "http://18.205.122.159:2000/api/v1";
    Network network = Neo.MAIN_NET;
    int coinType = 148;
    String seedKey = "ed25519 seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bseed;
    String privateKey;
    String walletAddress;
    String Wif;
    Wallet neoWallet;
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
        account = new Account(keyPair, 3009998980382720L);
        walletAddress = keyPair.getAccountId();
        privateKey = account.getKeypair().getPublicKey().toString();

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

    public void getTransactions(NRLCallback callback) {
        this.checkTransactions(callback);
    }

    private void checkBalance(NRLCallback callback) {
        this.walletAddress = "GC7YV53NUWB7YZVE4UZM22FZIDFNTIU3L4BFTWX3X5XTKBP2ZAABE6RY";
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

    private void checkTransactions(NRLCallback callback) {
        this.walletAddress = "GC7YV53NUWB7YZVE4UZM22FZIDFNTIU3L4BFTWX3X5XTKBP2ZAABE6RY";
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

    public void getOperation(NRLCallback callback) {
        this.walletAddress = "GC7YV53NUWB7YZVE4UZM22FZIDFNTIU3L4BFTWX3X5XTKBP2ZAABE6RY";
        String url_getTransaction = url_server + "/account/payments/" + this.walletAddress;

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
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    public void createTransaction(long amount, String destinationAddress) {
        KeyPair destination = KeyPair.fromAccountId(destinationAddress);
        Account sourceAccount = new Account(keyPair, 3009998980382720L);
        PaymentOperation operation = new PaymentOperation.Builder(destination, new AssetTypeNative(), amount).build();

        Transaction transaction = new Transaction.Builder(sourceAccount)
                .addOperation(operation)
                .addMemo(Memo.text("Java FTW!"))
                .build();

        transaction.sign(keyPair);

        System.out.println(transaction.toEnvelopeXdrBase64());
    }
}
