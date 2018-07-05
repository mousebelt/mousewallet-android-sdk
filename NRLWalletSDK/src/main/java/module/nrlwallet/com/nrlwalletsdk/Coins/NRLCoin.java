package module.nrlwallet.com.nrlwalletsdk.Coins;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.List;

import io.github.novacrypto.bip32.Network;

/*
* https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
* derived path:
* m / purpose' / coin_type' / account' / change / address_index
* Ex.  m / 44' / 60' / 0' / 0
* https://github.com/satoshilabs/slips/blob/master/slip-0044.md
* */

public class NRLCoin {
    private byte[] Seed;
    public Network network;
    private int coinType;
    private String SeedKey;
    private String Curve;

    private String masterPrivateKey;
    private String pathPrivateKey;

    private String address;
    private String wif;
    private String balance;
    private JSONArray transactions;
    final String HMAC_SHA512 = "HmacSHA512";

    public NRLCoin(byte[] seed, Network network, int coinType, String seedKey, String curve) {
        this.Seed = seed;
        this.network = network;
        this.coinType = coinType;
        this.SeedKey = seedKey;
        this.Curve = curve;
    }

    //should be overrided
    void generateAddress() {

    }

    byte[] generatePublicKeyFromPrivateKey(byte[] privateKey) {
        return privateKey;
    }


    public String getPublicKey() {
        return this.masterPrivateKey;
    }

    public String getPrivateKey() {
        return this.wif;
    }

    public String getAddress() {
        return this.address;
    }
}
