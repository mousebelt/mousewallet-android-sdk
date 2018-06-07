package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import neoutils.Neoutils;
import neoutils.Wallet;

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
        String address = this.neoWallet.getAddress();
        return address;
    }
}
