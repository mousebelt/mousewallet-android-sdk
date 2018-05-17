package module.nrlwallet.com.sdktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.math.BigInteger;

import module.nrlwallet.com.nrlwalletsdk.AddressGenerate;
import module.nrlwallet.com.nrlwalletsdk.ExtendedPrivateKeyBIP32;
import module.nrlwallet.com.nrlwalletsdk.GenerateMnemonic;
import module.nrlwallet.com.nrlwalletsdk.Language.English;
import module.nrlwallet.com.nrlwalletsdk.MnemonicToSeed;
import module.nrlwallet.com.nrlwalletsdk.Network.CoinType;

public class MainActivity extends AppCompatActivity {
    private String strMnemonic;
    private String strSeed;
    private String strRootKey;
    private String strAddress;
    private String strPrivateKey;
    private String strPublicKey;
    private byte[] bSeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getMnemonic();
        this.generateSeed();
        this.generateRootKey();
        this.generateExtendedPrivateKey();

        System.out.println("************----------- Mnemonic : " + strMnemonic);
        System.out.println("************----------- Seed : " + strSeed);
        System.out.println("************----------- BIP32 Root Key : " + strRootKey);
        System.out.println("************----------- Extended Private Key : " + strPrivateKey);
        System.out.println("************----------- Extended Public Key : " + strPublicKey);
        System.out.println("************----------- Address1 : " + strAddress);
    }

    /*
    * Generate Mnemonic
    * used BIP39
    * */

    private void getMnemonic() {
        StringBuilder sb = new StringBuilder();
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);
        this.strMnemonic = sb.toString();
    }

    /*
     * Generate Seed from mnemonic
     * used BIP39
     * */

    private void generateSeed() {
        this.strSeed = new MnemonicToSeed().calculateSeed(this.strMnemonic, "");
    }

    /*
     * Generate Root key
     * used BIP32
     * */

    private void generateRootKey() {
        this.bSeed = new MnemonicToSeed().calculateSeedByte(this.strMnemonic, "");
        this.strRootKey = new ExtendedPrivateKeyBIP32().getRootKey(bSeed, 1, 1);
    }

    /*
     * Generate Private/Public key and Address
     * used BIP44
     * */
    private void generateExtendedPrivateKey() {
        AddressGenerate addressGenerate = new AddressGenerate(CoinType.ETHEREUM, 0, 0 );
        String[] keyArray = addressGenerate.getBIP32ExtendedKeyArray(bSeed);
        this.strPrivateKey = keyArray[0];
        this.strPublicKey = keyArray[1];

        this.strAddress = keyArray[2];
    }
}