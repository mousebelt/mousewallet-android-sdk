package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Account;
import module.nrlwallet.com.nrlwalletsdk.Stellar.AssetTypeNative;
import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Memo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.PaymentOperation;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import neoutils.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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
    String balance;
    OkHttpClient client = new OkHttpClient();

    public NRLStellar(byte[] bseed) {
        super(bseed, Neo.MAIN_NET, 888, "ed25519 seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
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

    public void getBalance() {
        String url_getbalance = "/api/v1/balance/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    private void getTransactions() {
    }

    public void createWallet(Date date, boolean knew) {
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
