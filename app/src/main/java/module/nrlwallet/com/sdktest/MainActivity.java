package module.nrlwallet.com.sdktest;

import android.app.Activity;
import android.os.Bundle;

import module.nrlwallet.com.nrlwalletsdk.Coins.NRLBitcoin;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLEthereum;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLLite;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLNeo;
import module.nrlwallet.com.nrlwalletsdk.Utils.GenerateMnemonic;
import module.nrlwallet.com.nrlwalletsdk.Language.English;
import module.nrlwallet.com.nrlwalletsdk.Utils.MnemonicToSeed;

public class MainActivity extends Activity {

//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getEthereumWallet();
        this.getLitecoinWallet();
        this.getBitcoinWallet();
    }

    private void getEthereumWallet() {
        StringBuilder sb = new StringBuilder();
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);
        String strMnemonic = sb.toString();
//
        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLEthereum nrlEthereum = new NRLEthereum(bseed);
        String ethRootKey = nrlEthereum.getRootKey();
        String ethPrivateKey = nrlEthereum.getPrivateKey();
        String ethAddress = nrlEthereum.getAddress();
        System.out.println("************----------- Mnemonic : " + strMnemonic);
        System.out.println("************----------- Seed : " + seed);
        System.out.println("************----------- BIP32 Root Key : " + ethRootKey);
        System.out.println("************----------- Extended Private Key : " + ethPrivateKey);
        System.out.println("************----------- ETH address : " + ethAddress);

    }

    private void getNeoWallet() {
        StringBuilder sb = new StringBuilder();
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);
        String strMnemonic = sb.toString();

        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLNeo nrlNeo = new NRLNeo(bseed);
        String neoPrivateKey = nrlNeo.getPrivateKey();
        String neoAddress = nrlNeo.getAddress();
        System.out.println("************----------- Mnemonic : " + strMnemonic);
        System.out.println("************----------- Seed : " + seed);
        System.out.println("************----------- NEO Private Key : " + neoPrivateKey);
        System.out.println("************----------- NEO address     : " + neoAddress);
    }

    private void getBitcoinWallet() {
        StringBuilder sb = new StringBuilder();
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);
        String strMnemonic = sb.toString();

        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLBitcoin nrlBitcoin = new NRLBitcoin(bseed);

    }
    private void getLitecoinWallet() {
        StringBuilder sb = new StringBuilder();
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);
        String strMnemonic = sb.toString();

        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLLite nrlLite = new NRLLite(bseed);

    }
}