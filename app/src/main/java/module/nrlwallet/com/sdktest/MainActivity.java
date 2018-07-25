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
import module.nrlwallet.com.nrlwalletsdk.Utils.Util;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;

public class MainActivity extends Activity {

    private String strMnemonic;
//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StringBuilder sb = new StringBuilder();
        //
        new GenerateMnemonic(English.INSTANCE).createMnemonic(sb::append);

        this.getLitecoinWallet(strMnemonic);

//        this.getEthereumWallet(strMnemonic);//DONE
//        this.getBitcoinWallet(strMnemonic);//DONE
//        this.getStellarWallet(strMnemonic);//DONE
//        this.getNeoWallet(strMnemonic);//DONE
    }

    private void getEthereumWallet(String strMnemonic) {
        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLEthereum nrlEthereum = new NRLEthereum(bseed, strMnemonic);
/*
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
*/
        //amount, toAddress, memo, fee, callback
//        nrlEthereum.createTransaction("500000000000000", "0x9aFEE7Af06290771F589381730312939c2657239", "", 0.00001, new NRLCallback(){
//            @Override
//            public void onFailure(Throwable t) {
//
//            }
//            @Override
//            public void onResponse(String response) {
//                System.out.println("************----------- ETH Balance     : " + response);
//
//            }
//            @Override
//            public void onResponseArray(JSONArray jsonArray) {
//
//            }
//        });
    }

    private void getNeoWallet(String strMnemonic) {

        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");
//        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        byte[] bseed = Util.stringToBytes(seed);

        NRLNeo nrlNeo = new NRLNeo(bseed, strMnemonic);
        //need sync
/*
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

*/
        nrlNeo.createTransaction(1, "AQhwKFBVN1DicQkdqRaDGaDbxhXQEKzzxX", "", 0.001, new NRLCallback(){
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
        String btcPrivateKey = nrlBitcoin.getBalance();
        String btcAddress = nrlBitcoin.getAddress();
        /*
        nrlBitcoin.getBalance(new NRLCallback() {
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
        nrlBitcoin.getTransactions(new NRLCallback() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onResponseArray(JSONArray jsonArray) {

            }
        });*/
        nrlBitcoin.setTransaction(30000, "1Ncbaw4SbQt2UJYhA3fJVzBg43Zwdk9w5L", new NRLCallback(){
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
    private void getLitecoinWallet(String strMnemonic) {

        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");

        NRLLite nrlLite = new NRLLite(bseed, strMnemonic, getApplicationContext());
//        String stlPrivateKey = nrlLite.getPrivateKey();
//        String stlAddress = nrlLite.getAddress();
//        System.out.println("************----------- Mnemonic : " + strMnemonic);
//        System.out.println("************----------- Seed : " + seed);
//        System.out.println("************----------- Lite Private Key : " + stlPrivateKey);
//        System.out.println("************----------- Lite address     : " + stlAddress);
        /*
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
        /*
        nrlLite.createTransaction("0.0001", "LeR3qsGMvP3bzQux7hj3LLoKfdDSHdtFo8", "", (long) 0.01, new NRLCallback() {
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
*/
    }

    private void getStellarWallet(String strMnemonic) {
//        byte[] bseed = new MnemonicToSeed().calculateSeedByte(strMnemonic, "");
        String seed = new MnemonicToSeed().calculateSeed(strMnemonic, "");
        byte[] bseed = Util.stringToBytes(seed);


        NRLStellar nrlStellar = new NRLStellar(bseed, seed);
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
        //amount : long,    destinationAddress : string, Callback
        nrlStellar.SendTransaction((long)1, "GCKYEVXDV5BRXGPUJ22FORT2WUNSXCQ6JH2PT36ZBMWLV44YTLVGONTP", new NRLCallback() {

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
