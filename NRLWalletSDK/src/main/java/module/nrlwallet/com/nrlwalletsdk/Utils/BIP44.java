package module.nrlwallet.com.nrlwalletsdk.Utils;

import module.nrlwallet.com.nrlwalletsdk.Common.ValidationException;

public class BIP44 {
    // Based on BIP044
    private static final int HIGH = 0x80000000;
    private final static int PURPOSE = 44 + HIGH;
    private final static int COIN_TYPE = 0 + HIGH;

    public final static ExtendedKey getKey(ExtendedKey m, int account, int change) throws ValidationException {
        return m.getChild(PURPOSE).getChild(COIN_TYPE).getChild(account + HIGH).getChild(change);
    }
    public final static ExtendedKey getKeyType(ExtendedKey m, int cointype, int account, int change) throws ValidationException {
        int _cointype = cointype + HIGH;
        return m.getChild(PURPOSE).getChild(_cointype).getChild(account + HIGH).getChild(change);
    }
}
