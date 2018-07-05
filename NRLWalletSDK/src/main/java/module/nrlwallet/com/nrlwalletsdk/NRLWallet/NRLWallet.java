package module.nrlwallet.com.nrlwalletsdk.NRLWallet;

import module.nrlwallet.com.nrlwalletsdk.Coins.NRLCoin;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLEthereum;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLNeo;
import module.nrlwallet.com.nrlwalletsdk.Network.CoinType;

public class NRLWallet {
    private NRLCoin coin;

    public NRLWallet(byte[] Seed, int coinType){
        switch (coinType){
            case CoinType.ETHEREUM:
//                this.coin = new NRLEthereum(Seed);
                break;
            case CoinType.NEO:
//                this.coin = new NRLNeo(Seed);
                break;
            default:
//                this.coin = new NRLEthereum(Seed);
        }
    }

    public String getPublicKey() {
        return this.coin.getPublicKey();
    }

    public String getPrivateKey() {
        return this.coin.getPrivateKey();
    }

    public String getAddress() {
        return this.coin.getAddress();
    }

    public void generateExternalKeyPair(int index) {
//        this.coin.generateExternalKeyPair
    }
}
