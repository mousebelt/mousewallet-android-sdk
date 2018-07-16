package module.nrlwallet.com.nrlwalletsdk.Coins;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.ChildNumber;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.DeterministicKey;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.HDUtils;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicKeyChain;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicSeed;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.UnreadableWalletException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicSeed;
import module.nrlwallet.com.nrlwalletsdk.Network.Ethereum;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NRLEthereum extends NRLCoin {
    String url_server = "https://eth.mousebelt.com/api/v1";
    Network network = Ethereum.MAIN_NET;
    int coinType = 60;
    String seedKey = "Bitcoin seed";
    String Mnemonic = "";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bSeed;
    String walletAddress;
    int count = 0;
    String privateKey;
    String privateKey_origin;
    JSONArray transactions = new JSONArray();
    Credentials credentials;

    public NRLEthereum(byte[] seed, String strMnemonic) {
        super(seed, Ethereum.MAIN_NET, 60, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        bSeed = seed;
        Mnemonic = strMnemonic;
//        this.init();
        this.createAddress();
    }

    private void createAddress() {
        DeterministicSeed seed = null;
        try {
            Long creationtime = new Date().getTime();
            seed = new DeterministicSeed(Mnemonic, null, "", creationtime);
            DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
            List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
            DeterministicKey key = chain.getKeyByPath(keyPath, true);
            BigInteger privKey = key.getPrivKey();

// Web3j
            credentials = Credentials.create(privKey.toString(16));
            this.privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
            privateKey_origin = "0x" + credentials.getEcKeyPair().getPrivateKey().toString(16);

            this.walletAddress = credentials.getAddress();
            this.getTransactionCount();
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
    }

//    private void init() {
//        addressIndex = BIP44.m()
//                .purpose44()
//                .coinType(coinType)
//                .account(0)
//                .external()
//                .address(0);
//
//        ExtendedPrivateKey root = ExtendedPrivateKey.fromSeed(bSeed, Ethereum.MAIN_NET);
//        String DerivedAddress = root
//                .derive("m/44'/60'/0'/0/0")
//                .neuter().p2pkhAddress();
//        System.out.println(DerivedAddress);
//        root.derive("m/44'/60'/0'/0/0");
//
//        this.rootKey = new ExtendedPrivateKeyBIP32().getRootKey(bSeed, CoinType.ETHEREUM);
//        ExtendedPrivateKey extendedPrivateKey = ExtendedPrivateKey.fromSeed(bSeed, Ethereum.MAIN_NET);
//        ExtendedPrivateKey child = extendedPrivateKey.derive(addressIndex, AddressIndex.DERIVATION);
//        ExtendedPublicKey childPub = child.neuter();
//        extendedPrivateKey = child.extendedBase58();   //Extended Private Key
//        extendedPublicKey = childPub.extendedBase58();    //Extended Public Key
//        walletAddress = childPub.p2pkhAddress();
//        String str4 = childPub.p2shAddress();
//        this.getTransactionCount();
//    }

    @Override
    public String getAddress() {
        return this.walletAddress;
    }

    @Override
    public String getPrivateKey() {
        return this.privateKey;
    }

    public void getBalance(NRLCallback callback) {
        this.checkBalance(callback);
    }

    public void getTransactionCount() {
        String url_getbalance = url_server + "/address/gettransactioncount/" + this.walletAddress;
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
                            JSONObject data = jsonObj.getJSONObject("data");
                            JSONArray balances = data.getJSONArray("balances");
                            callback.onResponseArray(balances);
                            return;
//                            for(int i = 0; i < balances.length(); i++) {
//                                JSONObject obj = balances.getJSONObject(i);
//                                String ticker = obj.getString("symbol");
//                                if(ticker.equals("ETH")){
//                                    balance = obj.getString("balance");
//                                    callback.onResponse(balance);
//                                    return;
//                                }
//                            }
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

    public void getTransactionsJson(NRLCallback callback) {
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
                } else {
                }
            }
        });
    }

    public void getTransactions(NRLCallback callback) {
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
                            JSONArray arrTransactions = new JSONArray();
                            for(int i = 0; i < transactions.length(); i++ ) {
                                JSONObject object = transactions.getJSONObject(i);
                                JSONObject retunValue = new JSONObject();
                                retunValue.put("txid", object.getString("blockHash"));
                                if(object.getString("from").equals(walletAddress)){
                                    retunValue.put("value", "-" + object.getString("value"));
                                }else {
                                    retunValue.put("value", "+" + object.getString("value"));
                                }
                                arrTransactions.put(retunValue);
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

    public void createTransaction(String amount, String address, String memo, double fee, NRLCallback callback) {
        BigInteger nonce = BigInteger.valueOf(5);//this.count);
        BigInteger gas_price = BigInteger.valueOf((long) 5500000000L);
        String amount_data = "0x" + amount;
        BigInteger send_amount = BigInteger.valueOf(Long.valueOf(amount));
        // gas price 1,000,000,000 ===10e9
        // amount 1ETH = 1,000,000,000,000,000,000 WEI ==== 10e18
        //nonce, <gas price>, <gas limit>, <toAddress>, <value>
        RawTransaction rawTransaction;
        if(memo.equals("ETH")){
            rawTransaction = RawTransaction.createEtherTransaction(nonce, gas_price, BigInteger.valueOf(21000), address, send_amount);
        } else {
            rawTransaction = RawTransaction.createTransaction(nonce, gas_price, BigInteger.valueOf(41000), address,send_amount, memo);
        }

//        credentials = Credentials.create(this.getPrivateKey());
//        credentials = Credentials.create(this.privateKey_origin);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String str_Raw = "";
        str_Raw = bytesToHex(signedMessage);
        System.out.println("************----------- : " + str_Raw);
        String url_sendTransaction = url_server + "/sendsignedtransaction/";

        String json = "{\"raw\":\"" + str_Raw + "\"}";
        RequestBody body = RequestBody.create(HTTPRequest.JSON, json);

        new HTTPRequest().run(url_sendTransaction, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String body =   (response.body().string());
                    try {
                        JSONObject object = new JSONObject(body);
                        String transactionID = object.getString("data");
                        count++;
                        callback.onResponse(transactionID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }

                }else{
                    callback.onResponse("Failed");
                }

            }
        });

    }

    public char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public  String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
