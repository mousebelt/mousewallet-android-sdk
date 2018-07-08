package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.github.novacrypto.bip32.Network;
import jnr.ffi.Struct;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import neoutils.Neoutils;
import neoutils.RawTransaction;
import neoutils.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class NRLNeo extends NRLCoin {
    String url_server = "http://54.152.5.218/api/v1";

    Network network = Neo.MAIN_NET;
    int coinType = 888;
    String seedKey = "Nist256p1 seed";
    String curve = "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551";
    byte[] bseed;
    String privateKey;
    String walletAddress;
    String Wif;
    Wallet neoWallet;
    JSONArray trnasactions;
    public String balance;

    private NRLCallback callback;

    public NRLNeo(byte[] bseed) {
        super(bseed, Neo.MAIN_NET, 888, "Nist256p1 seed", "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551");
        this.bseed = bseed;
        this.init();
    }

    private void init() {

        Mac sha512_HMAC = null;
        String path = "m/44'/888'/0'/0/0";

        final byte[] b_seedkey = seedKey.getBytes();
        final byte[] b_path = path.getBytes();

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


//            SecretKeySpec keySpec1 = new SecretKeySpec(bseed, HMAC_SHA512);
//            sha512_HMAC.init(keySpec1);
//            byte [] mac_data1 = sha512_HMAC.doFinal(b_path);
//            byte[] slice1 = Arrays.copyOfRange(mac_data1, 32, 64);
//            String b1 = Neoutils.bytesToHex(slice1);
//            neoWallet = Neoutils.generateFromPrivateKey(b1);
//            walletAddress = neoWallet.getAddress();





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
//        this.walletAddress = "AJXPjfQ6EmRpRsoS94EzrfSPDUc8m8Zio5";
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
        //AeVkPRiies6pMdWJoh78eHR9s6bGp5AGJf
//        this.walletAddress = "AJXPjfQ6EmRpRsoS94EzrfSPDUc8m8Zio5";
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
        //AeVkPRiies6pMdWJoh78eHR9s6bGp5AGJf
//        this.walletAddress = "AJXPjfQ6EmRpRsoS94EzrfSPDUc8m8Zio5";
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
    public void createTransaction(long amount, String address, String memo, long fee) {
        String str_network = network.toString();
        String str_hash = neoWallet.hashCode() + "";
        String str_wif = neoWallet.getWIF();
        double d_amount = Double.longBitsToDouble(amount);
        double d_fee = Double.longBitsToDouble(fee);
        try {
            RawTransaction transaction = Neoutils.mintTokensRawTransactionMobile(str_network, str_hash, str_wif, address, d_amount, memo, d_fee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
