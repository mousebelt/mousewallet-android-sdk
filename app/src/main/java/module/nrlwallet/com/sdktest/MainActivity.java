package module.nrlwallet.com.sdktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import module.nrlwallet.com.nrlwalletsdk.Coins.NRLEthereum;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLNeo;
import module.nrlwallet.com.nrlwalletsdk.Utils.GenerateMnemonic;
import module.nrlwallet.com.nrlwalletsdk.Language.English;
import module.nrlwallet.com.nrlwalletsdk.Utils.MnemonicToSeed;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getEthereumWallet();
        this.getNeoWallet();
    }

    private void getEthereumWallet() {
        StringBuilder sb = new StringBuilder();
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);
        String strMnemonic = sb.toString();

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
}