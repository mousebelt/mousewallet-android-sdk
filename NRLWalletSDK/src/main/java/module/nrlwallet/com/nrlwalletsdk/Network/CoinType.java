package module.nrlwallet.com.nrlwalletsdk.Network;

public class CoinType {
    public static final int BITCOIN         = 0;
    public static final int LITECOIN        = 2;
    public static final int NEM             = 43;
    public static final int ETHEREUM        = 60;
    public static final int ETHREUMCLASSIC  = 61;
    public static final int MONERO          = 128;
    public static final int ZCASH           = 133;
    public static final int LISK            = 134;
    public static final int BITCOINCASH     = 145;
    public static final int STELLAR         = 148;
    public static final int NEO             = 888;

    public final int coinType;

    public CoinType(int coinType) {
        this.coinType = coinType;
    }
}

