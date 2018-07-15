package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoin.Secp256k1Context;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.ExtendedPublicKey;
import io.github.novacrypto.bip32.Network;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip32.networks.Litecoin;
import io.github.novacrypto.bip44.Account;
import io.github.novacrypto.bip44.BIP44;
import javassist.bytecode.ByteArray;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Secp256k1;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import neoutils.NEP5;
import neoutils.Neoutils;
import neoutils.RawTransaction;
import neoutils.SharedSecret;
import neoutils.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class NRLNeo extends NRLCoin {
    String url_server = "https://neo.mousebelt.com/api/v1";

    Network network = Neo.MAIN_NET;
    int coinType = 888;
    String Mnemonic = "";
    String seedKey = "Nist256p1 seed";
    String curve = "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551";
    byte[] bseed;
    String privateKey;
    String walletAddress;
    String Wif;
    Wallet neoWallet;
    JSONArray trnasactions;
    public String balance;
    String neoAssetID = "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
    String gasAssetID = "602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7";

    public NRLNeo(byte[] bseed, String mnemonic) {
        super(bseed, Neo.MAIN_NET, 888, "Nist256p1 seed", "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551");
        this.bseed = bseed;
        this.Mnemonic = mnemonic;
        this.init();
    }

    private void test() {
        byte[] aaa = SecureRandom.getSeed(32);
        try {
            neoWallet = Neoutils.generateFromWIF("L59tWNmwh6RsmijTLGmkq8ZKuJyocH41mCFBLVrCbjMwP6tWE8xh");
            byte[] b_privatekey = neoWallet.getPrivateKey();
            privateKey = Neoutils.bytesToHex(b_privatekey);
            byte[] b_publickey = neoWallet.getPublicKey();
//            neoWallet = Neoutils.generateFromPrivateKey(this.privateKey);
            walletAddress = neoWallet.getAddress();
            Boolean isValid = Neoutils.validateNEOAddress(walletAddress);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Mac sha512_HMAC = null;
        String path = "m/44'/888'/0'/0/0";

        final byte[] b_seedkey = seedKey.getBytes();
        byte[] buf = path.getBytes(Charset.defaultCharset());

        try {
            sha512_HMAC = Mac.getInstance(HMAC_SHA512);

            SecretKeySpec keySpec = new SecretKeySpec(bseed, HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            byte [] mac_data = sha512_HMAC.doFinal(buf);
            byte[] slice = Arrays.copyOfRange(mac_data, 0, 32);
            this.privateKey = Neoutils.bytesToHex(slice);

            neoWallet = Neoutils.generateFromPrivateKey(this.privateKey);
            walletAddress = neoWallet.getAddress();
            Boolean isValid = Neoutils.validateNEOAddress(walletAddress);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return this.walletAddress;
    }
    public void getBalance(NRLCallback callback) {
        checkBalance(callback);
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
                            JSONArray balances = data.getJSONArray("balance");
                            for(int i = 0; i < balances.length(); i++) {
                                JSONObject obj = balances.getJSONObject(i);
                                String ticker = obj.getString("ticker");
                                if(ticker.equals("NEO")){
                                    balance = obj.getString("value");
                                    callback.onResponse(balance);
                                    return;
                                }
                            }

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
                            trnasactions = data.getJSONArray("result");
                            callback.onResponseArray(trnasactions);
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
                            trnasactions = data.getJSONArray("result");
                            JSONArray arrTransaction = new JSONArray();
                            for(int i = 0; i < trnasactions.length(); i ++) {
                                JSONObject transaction = trnasactions.getJSONObject(i);
                                JSONArray vin = transaction.getJSONArray("vin");
                                JSONArray vout = transaction.getJSONArray("vout");
                                Double transactionVal = new Double(0);
                                Double vinVal = new Double(0);
                                Double voutVal = new Double(0);
                                for(int j = 0; j < vin.length(); j++ ) {
                                    JSONObject vinObj = vin.getJSONObject(j);
                                    JSONObject addresses = vinObj.getJSONObject("address");
                                    if(addresses.getString("address").equals(walletAddress)) {
                                        vinVal += Double.parseDouble(addresses.getString("value"));
                                    }
                                }
                                for(int j = 0; j < vout.length(); j++ ) {
                                    JSONObject voutObjArr = vout.getJSONObject(j);
                                    if(voutObjArr.getString("address").equals(walletAddress)) {
                                        voutVal += Double.parseDouble(voutObjArr.getString("value"));
                                    }
                                }
                                transactionVal = vinVal - voutVal;
                                JSONObject transactionData = new JSONObject();
                                transactionData.put("txid", transaction.getString("txid"));
                                transactionData.put("value", transactionVal);
                                arrTransaction.put(transactionData);
                            }
                            callback.onResponseArray(arrTransaction);
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
    //network string, scriptHash string, wif string, sendingAssetID string, amount float64, remark string, networkFeeAmountInGAS float64
    public void createTransaction(double amount, String address, String memo, double fee, NRLCallback callback) {


        String str_network = "private";//neoAssetID;//"main";//network.toString();
        String str_hash = "5f03828cb45198eedd659d264b6d3a1c889978ce";//neoWallet.hashCode() + "";
        String str_wif = neoWallet.getWIF();
        double d_amount =  amount;//Double.longBitsToDouble(amount);
        double d_fee = fee;//Double.longBitsToDouble((long) fee);
        String assetID = "NEO";
        try {
            RawTransaction transaction = Neoutils.mintTokensRawTransactionMobile(str_network, str_hash, str_wif, assetID, d_amount, memo, d_fee);
//            callback.onResponse(transaction.getTXID());
            callback.onResponse("success");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e);
        }

    }
}
