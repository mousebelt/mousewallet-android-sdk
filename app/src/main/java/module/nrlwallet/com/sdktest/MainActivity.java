package module.nrlwallet.com.sdktest;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONArray;

import module.nrlwallet.com.nrlwalletsdk.Coins.NRLBitcoin;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLEthereum;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLLite;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLNeo;
import module.nrlwallet.com.nrlwalletsdk.Coins.NRLStellar;
import module.nrlwallet.com.nrlwalletsdk.Utils.GenerateMnemonic;
import module.nrlwallet.com.nrlwalletsdk.Language.English;
import module.nrlwallet.com.nrlwalletsdk.Utils.MnemonicToSeed;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import module.nrlwallet.core.BRCoreKey;
import module.nrlwallet.core.BRCoreMasterPubKey;

public class MainActivity extends Activity {

    private String strMnemonic;
//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StringBuilder sb = new StringBuilder();
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);
//        strMnemonic = "tone absurd popular virus fatal possible skirt local head open siren damp";//sb.toString();
        strMnemonic = "cost alpha light gravity result unique multiply stadium fitness catalog diesel beauty";//sb.toString();
//        strMnemonic = "tape have dawn eagle ginger wagon couple sunset okay gentle curve put";//sb.toString();

//        this.getEthereumWallet(strMnemonic);//done
        this.getLitecoinWallet(strMnemonic);//okkkk------
//        this.getBitcoinWallet(strMnemonic);//okkkk
//        this.getNeoWallet(strMnemonic);//okkkkk----
//        this.getStellarWallet(strMnemonic);//okkkk
    }

    private void getEthereumWallet(String strMnemonic) {
        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLEthereum nrlEthereum = new NRLEthereum(bseed, strMnemonic);
        String ethRootKey = nrlEthereum.getRootKey();
        String ethPrivateKey = nrlEthereum.getPrivateKey();
        String ethAddress = nrlEthereum.getAddress();
        nrlEthereum.getBalance(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }
            @Override
            public void onResponse(String response) {
                System.out.println("************----------- ETH Balance     : " + response);

            }
            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });

        nrlEthereum.getTransactions(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });

        //amount, toAddress, memo, fee, callback
        nrlEthereum.createTransaction("0.001", "0xAf85f3AE0CE48c4C6c9Ad462761F6ca60f5087f1", "", 0.00001, new NRLCallback(){
            @Override
            public void onFailure(Throwable t) {

            }
            @Override
            public void onResponse(String response) {
                System.out.println("************----------- ETH Balance     : " + response);

            }
            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
    }

    private void getNeoWallet(String strMnemonic) {

        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLNeo nrlNeo = new NRLNeo(bseed);
        String neoPrivateKey = nrlNeo.getPrivateKey();
        String neoAddress = nrlNeo.getAddress();
        //need sync
        nrlNeo.getBalance(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }
            @Override
            public void onResponse(String response) {
                System.out.println("************----------- NEO Balance     : " + response);

            }
            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
        nrlNeo.getTransactions(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
        System.out.println("************----------- Mnemonic : " + strMnemonic);
        System.out.println("************----------- Seed : " + seed);
        System.out.println("************----------- NEO Private Key : " + neoPrivateKey);
        System.out.println("************----------- NEO address     : " + neoAddress);

        nrlNeo.createTransaction(0.001, "ATdP62ufbPWhcs3avSCSTX79cbAJ4STHVY", "", 0.00001, new NRLCallback(){
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
    }

    private void getBitcoinWallet(String strMnemonic) {

        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLBitcoin nrlBitcoin = new NRLBitcoin(bseed, strMnemonic);
        String btcPrivateKey = nrlBitcoin.getPrivateKey();
        String btcAddress = nrlBitcoin.getAddress();
        String btcBalance = nrlBitcoin.getBalance();
        nrlBitcoin.getTransctions(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
        System.out.println("************----------- Mnemonic : " + strMnemonic);
        System.out.println("************----------- Seed : " + seed);
        System.out.println("************----------- BTC Private Key : " + btcPrivateKey);
        System.out.println("************----------- BTC address     : " + btcAddress);
        System.out.println("************----------- BTC balance     : " + btcBalance);
    }
    private void getLitecoinWallet(String strMnemonic) {

        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLLite nrlLite = new NRLLite(bseed, strMnemonic);
        String stlPrivateKey = nrlLite.getPrivateKey();
        String stlAddress = nrlLite.getAddress();
        System.out.println("************----------- Mnemonic : " + strMnemonic);
        System.out.println("************----------- Seed : " + seed);
        System.out.println("************----------- Lite Private Key : " + stlPrivateKey);
        System.out.println("************----------- Lite address     : " + stlAddress);
        nrlLite.getBalance(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });

        nrlLite.getTransactions(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });

        nrlLite.createTransaction((long)0.0001, "LZSTRc6imhZLuz9aDQs8GTrLw3cjBHSMzJ", "", (long) 0.01, new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
    }

    private void getStellarWallet(String strMnemonic) {
        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLStellar nrlStellar = new NRLStellar(bseed);
        String stlPrivateKey = nrlStellar.getPrivateKey();
        String stlAddress = nrlStellar.getAddress();
        System.out.println("************----------- Mnemonic : " + strMnemonic);
        System.out.println("************----------- Seed : " + seed);
        System.out.println("************----------- Stellar Private Key : " + stlPrivateKey);
        System.out.println("************----------- Stellar address     : " + stlAddress);

        nrlStellar.getBalance(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }
            @Override
            public void onResponse(String response) {
                System.out.println("************----------- NEO Balance     : " + response);

            }
            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
        nrlStellar.getTransactions(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
        nrlStellar.createTransaction((long)0.001, "GBQK3OOKZBMIHDD7ODLYHDRPJ4TM43345UCYIV77OSWRQDEC5DGX2JNG", new NRLCallback() {

            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });
    }
}