package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
import neoutils.Neoutils;
import neoutils.RawTransaction;
import neoutils.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class NRLNeo extends NRLCoin {

    Network network = Neo.MAIN_NET;
    int coinType = 888;
    String seedKey = "Nist256p1 seed";
    String curve = "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551";
    byte[] bseed;
    String privateKey;
    String walletAddress;
    String Wif;
    Wallet neoWallet;
    String balance;

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

            SecretKeySpec keySpec1 = new SecretKeySpec(b_seedkey, HMAC_SHA512);
            sha512_HMAC.init(keySpec1);
            byte [] mac_data1 = sha512_HMAC.doFinal(b_path);
            byte[] slice1 = Arrays.copyOfRange(mac_data1, 0, 32);
            String b1 = Neoutils.bytesToHex(slice1);
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

    public void getBalance() {
        String url_getbalance = "/balance/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    balance =   (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(balance);
                        String sss = jsonObj.get("msg").toString();
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


    public void getTransactions() {
        String url_getTransaction = "/address/txs/" + this.walletAddress;
        new HTTPRequest().run(url_getTransaction, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    balance =   (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(balance);
                        String sss = jsonObj.get("msg").toString();
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
