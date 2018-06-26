package module.nrlwallet.com.nrlwalletsdk.Key;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Arrays;

import module.nrlwallet.com.nrlwalletsdk.Coins.NRLCoin;

public class NRLPrivateKey {
    private byte[] raw;
    private byte[] chainCode;
    private int depth;
    private int fingerprint;
    private int childIndex;
    private NRLCoin coin;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public NRLPrivateKey(NRLCoin coin, String seed, byte[] privkey){
        this.coin = coin;
        this.raw = Arrays.copyOfRange(privkey, 0, 32);
        this.chainCode = Arrays.copyOfRange(privkey, 32, 64);
        this.depth = 0;
        this.fingerprint = 0;
        this.childIndex = 0;
    }

    private void init(byte[] nrlPribateKey, byte[] chainCode, int depth, int fingerprint, int childIndex, NRLCoin coin) {
        this.raw = nrlPribateKey;
        this.chainCode = chainCode;
        this.depth = depth;
        this.fingerprint = fingerprint;
        this.childIndex = childIndex;
        this.coin = coin;
    }

    public String extended() {
        Object extendedPrivateKeyData = new Object();
//        extendedPrivateKeyData += this.coin.network.
        return "";
    }
}
