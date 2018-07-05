package module.nrlwallet.com.nrlwalletsdk.Coins;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
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
import java.util.List;

import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.ExtendedPublicKey;
import io.github.novacrypto.bip32.Network;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip44.Account;
import io.github.novacrypto.bip44.AddressIndex;
import io.github.novacrypto.bip44.BIP44;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Secp256k1;
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
    String url_server = "http://18.232.254.235/api/v1";
    Network network = Ethereum.MAIN_NET;
    int coinType = 60;
    String seedKey = "Bitcoin seed";
    String Mnemonic = "";
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
            seed = new DeterministicSeed(Mnemonic, null, "", 1409478661L);
            DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
            List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
            DeterministicKey key = chain.getKeyByPath(keyPath, true);
            BigInteger privKey = key.getPrivKey();

// Web3j
            Credentials credentials = Credentials.create(privKey.toString(16));
            walletAddress = credentials.getAddress();
            extendedPrivateKey = credentials.getEcKeyPair().getPrivateKey() + "";

            this.getTransactionCount();
            System.out.println(credentials.getAddress());
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        addressIndex = BIP44.m()
                .purpose44()
                .coinType(coinType)
                .account(0)
                .external()
                .address(0);

        ExtendedPrivateKey root = ExtendedPrivateKey.fromSeed(bSeed, Ethereum.MAIN_NET);
        String DerivedAddress = root
                .derive("m/44'/60'/0'/0/0")
                .neuter().p2pkhAddress();
        System.out.println(DerivedAddress);
        root.derive("m/44'/60'/0'/0/0");

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

    public void getGasPrice() {

    }
    private void getTransactionCount() {
        this.walletAddress = "0xC400b9D93A23b0be5d41ab337aD605988Aef8463";
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
        this.walletAddress = "0xC400b9D93A23b0be5d41ab337aD605988Aef8463";
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
                            for(int i = 0; i < balances.length(); i++) {
                                JSONObject obj = balances.getJSONObject(i);
                                String ticker = obj.getString("symbol");
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
        this.walletAddress = "0xC400b9D93A23b0be5d41ab337aD605988Aef8463";
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

    public void createTransaction(String amount, String address, String memo, long fee) {
//////// PLEASE MAKE GASEPRICE
//        tx = try EthereumTransaction(
//                nonce: EthereumQuantity(quantity: BigUInt(nonce)),
//        gasPrice: EthereumQuantity(quantity: BigUInt(fee)),
//        gas: EthereumQuantity(quantity: BigUInt(ethereumGasAmount)),
//        to: EthereumAddress(hex: to, eip55: false),
//        value: EthereumQuantity(quantity: BigUInt(value))
//)
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
        String url_sendTransaction = url_server + "/sendsignedtransaction/";
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
