package module.nrlwallet.com.nrlwalletsdk.Utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class BIP39 {
    private final static int HASH_SIZE = 512; // 512 bits
    private final static int PBKDF2_ITERATIONS = 2048;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static byte[] getSeed(String mnemonic, String passphrase) {

        try {
            mnemonic = Normalizer.normalize(mnemonic, Normalizer.Form.NFKD);
            passphrase = Normalizer.normalize("mnemonic" + passphrase, Normalizer.Form.NFKD);

            byte salt[] = passphrase.getBytes(StandardCharsets.UTF_8);

            byte[] hash = PBKDF2(mnemonic.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_SIZE);

            return hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] PBKDF2(char[] password, byte[] salt, int iterations, int keysize) throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keysize);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512"); // this throws a NoSuchAlgorithmException if I replace with "PBKDF2WithHmacSHA512"
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return hash;
    }

    public static byte[] sha256hash(byte[]... data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            for (byte[] b : data) {
                if (b != null) {
                    md.update(b);
                }
            }

            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
