package module.nrlwallet.com.nrlwalletsdk.Coins;

import org.bitcoinj.core.ECKey;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.ExtendedPublicKey;
import io.github.novacrypto.bip32.Network;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip44.Account;
import io.github.novacrypto.bip44.AddressIndex;
import io.github.novacrypto.bip44.BIP44;
import module.nrlwallet.com.nrlwalletsdk.Network.CoinType;
import module.nrlwallet.com.nrlwalletsdk.Network.Ethereum;
import module.nrlwallet.com.nrlwalletsdk.Utils.ExtendedPrivateKeyBIP32;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import module.nrlwallet.core.BRCoreKey;
import module.nrlwallet.core.BRCoreMasterPubKey;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NRLEthereum extends NRLCoin {
    Network network = Ethereum.MAIN_NET;
    int coinType = 60;
    String seedKey = "Bitcoin seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bSeed;
    String rootKey;
    AddressIndex addressIndex;
    String extendedPrivateKey;
    String extendedPublicKey;
    String walletAddress;
    String balance = "0";
    int count = 0;
    ExtendedPrivateKey privateKey;
    JSONArray transactions = new JSONArray();

    public NRLEthereum(byte[] seed) {
        super(seed, Ethereum.MAIN_NET, 60, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        bSeed = seed;
        this.init();
    }

    private void test() {


    }

    private void init() {
        ExtendedPrivateKey root = ExtendedPrivateKey.fromSeed(bSeed, network);
        addressIndex = BIP44.m()
                .purpose44()
                .coinType(coinType)
                .account(0)
                .external()
                .address(0);

        this.rootKey = new ExtendedPrivateKeyBIP32().getRootKey(bSeed, CoinType.ETHEREUM);
        privateKey = ExtendedPrivateKey.fromSeed(bSeed, Ethereum.MAIN_NET);
        ExtendedPrivateKey child = privateKey.derive(addressIndex, AddressIndex.DERIVATION);
        ExtendedPublicKey childPub = child.neuter();
        extendedPrivateKey = child.extendedBase58();   //Extended Private Key
        extendedPublicKey = childPub.extendedBase58();    //Extended Public Key
        walletAddress = childPub.p2pkhAddress();
        String str4 = childPub.p2shAddress();
        this.getTransactionCount();
    }

    public String getRootKey() {
        return this.rootKey;
    }

    @Override
    public String getAddress() {
        return this.walletAddress;
    }

    @Override
    public String getPrivateKey() {
        return this.extendedPrivateKey;
    }

    public void getBalance(NRLCallback callback) {
        this.checkBalance(callback);
    }
    public void getTransactions(NRLCallback callback) {
        this.checkTransactions(callback);
    }

    private void getTransactionCount() {
        String url_getbalance = "/address/gettransactioncount/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result =   (response.body().string());
                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            count = jsonObj.getInt("data");
                        }else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Request not successful
                }
            }
        });
    }

    private void checkBalance(NRLCallback callback) {
        String url_getbalance = "/balance/" + this.walletAddress;
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
                            JSONObject data = jsonObj.getJSONObject("data");
                            JSONArray balances = data.getJSONArray("balance");
                            for(int i = 0; i < balances.length(); i++) {
                                JSONObject obj = balances.getJSONObject(i);
                                String ticker = obj.getString("ticker");
                                if(ticker.equals("ETH")){
                                    balance = obj.getString("balance");
                                    callback.onResponse(balance);
                                    return;
                                }
                            }

                        }else {
                            callback.onResponse("0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                } else {
                    // Request not successful
                    callback.onResponse("0");
                }
            }
        });
    }

    private void checkTransactions(NRLCallback callback) {
        String url_getTransaction = "/address/txs/" + this.walletAddress;
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

    public void createTransaction(String amount, String address, String memo, long fee) {

        BigInteger nonce = BigInteger.valueOf(this.count);
        BigInteger gas_price = BigInteger.valueOf(fee);
        //nonce, <gas price>, <gas limit>, <toAddress>, <value>
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gas_price, BigInteger.valueOf(21000), address, amount);

        Credentials credentials = Credentials.create(this.getPrivateKey());
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String str_Raw = "";
        try {
            str_Raw = new String(signedMessage, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url_sendTransaction = "/sendsignedtransaction/";
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("raw", str_Raw);

        RequestBody formBody = formBuilder.build();

        new HTTPRequest().run(url_sendTransaction, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }

}
