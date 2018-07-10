package module.nrlwallet.com.nrlwalletsdk.Utils;

import org.bitcoinj.core.Utils;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;

public class LitecoinNetParams extends AbstractBitcoinNetParams{
    /**
     * The string returned by getId() for the Litecoin main net netparams.
     */
    public static final String ID_LITECOIN_MAINNET = "org.bitcoinj.litecoin_mainnet";
    public static final int MAINNET_MAJORITY_WINDOW = MainNetParams.MAINNET_MAJORITY_WINDOW;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = MainNetParams.MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = MainNetParams.MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
    public LitecoinNetParams() {
        super();
        id = ID_LITECOIN_MAINNET;
        // Genesis hash is 12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2
        packetMagic = 0xfbc0b6db;

        maxTarget = Utils.decodeCompactBits(0x1e0fffffL);
        port = 9333;
        addressHeader = 48;
        p2shHeader = 5;
        acceptableAddressCodes = new int[]{addressHeader, p2shHeader};
        dumpedPrivateKeyHeader = 176;

        spendableCoinbaseDepth = 100;
        subsidyDecreaseBlockCount = 840000;

        genesisBlock.setTime(1317972665L);
        genesisBlock.setDifficultyTarget(0x1e0ffff0L);
        genesisBlock.setNonce(2084524493);

        String genesisHash = genesisBlock.getHashAsString();
//        checkState(genesisHash.equals("5155a7ed2219a75c0735c58b5d459c6d07d97917570e27b9d1d4546fb8431381"));
        alertSigningKey = Hex.decode("040184710fa689ad5023690c80f3a49c8f13f8d45b8c857fbcbc8bc4a8e4d3eb4b10f4d4604fa08dce601aaf0f470216fe1b51850b4acf21b179c45070ac7b03a9");

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        dnsSeeds = new String[]{
                "dnsseed.litecointools.com",
                "dnsseed.litecoinpool.org",
                "dnsseed.ltc.xurious.com",
                "dnsseed.koin-project.com",
                "dnsseed.weminemnc.com"
        };
        bip32HeaderPub = 0x0488B21E;
        bip32HeaderPriv = 0x0488ADE4;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }


    private static LitecoinNetParams instance;

    public static synchronized LitecoinNetParams get() {
        if (instance == null) {
            instance = new LitecoinNetParams();
        }
        return instance;
    }
}
