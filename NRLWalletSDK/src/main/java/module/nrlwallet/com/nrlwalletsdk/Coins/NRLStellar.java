package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Account;
import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.SignerKey;
import neoutils.Wallet;

@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class NRLStellar extends NRLCoin {
    Network network = Neo.MAIN_NET;
    int coinType = 888;
    String seedKey = "ed25519 seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bseed;
    String privateKey;
    String walletAddress;
    String Wif;
    Wallet neoWallet;
    KeyPair keyPair;
    Account account;

    public NRLStellar(byte[] bseed) {
        super(bseed, Neo.MAIN_NET, 888, "ed25519 seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        this.bseed = bseed;
        this.init();
    }

    private void init() {
        byte[] tmpseed = Arrays.copyOfRange(bseed, 0, 32);
        keyPair = KeyPair.fromSecretSeed(tmpseed);
        account = new Account(keyPair, 100L);
        walletAddress = account.getKeypair().getAccountId();
        privateKey = account.getKeypair().getPublicKey().toString();

        SignerKey a = account.getKeypair().getXdrSignerKey();

    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return walletAddress;
    }

    private void generateExternalKeyPair() {

    }

    public void createWallet(Date date, boolean knew) {

    }
}
