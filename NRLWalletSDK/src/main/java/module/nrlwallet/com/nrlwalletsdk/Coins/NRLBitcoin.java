package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Address;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Coin;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.DumpedPrivateKey;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.ECKey;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.InsufficientMoneyException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.NetworkParameters;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionConfidence;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionOutPoint;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.UTXO;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.ChildNumber;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.DeterministicKey;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.HDUtils;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.kits.WalletAppKit;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.params.MainNetParams;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicKeyChain;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicSeed;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.SendRequest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.UnreadableWalletException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.Wallet;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.WalletTransaction;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.listeners.WalletEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.github.novacrypto.bip32.ExtendedPublicKey;
import io.github.novacrypto.bip32.Network;
import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip32.networks.Litecoin;
import io.github.novacrypto.bip44.Account;
import io.github.novacrypto.bip44.AddressIndex;
import io.github.novacrypto.bip44.BIP44;
import jersey.repackaged.com.google.common.util.concurrent.MoreExecutors;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.NetworkParameters;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.kits.WalletAppKit;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.params.MainNetParams;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicSeed;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.UnreadableWalletException;
import module.nrlwallet.com.nrlwalletsdk.Common.ValidationException;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Base58Encode;
import module.nrlwallet.com.nrlwalletsdk.Utils.ExtendedKey;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.Utils.Util;
import module.nrlwallet.com.nrlwalletsdk.Utils.WIF;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import module.nrlwallet.core.BRCoreAddress;
import module.nrlwallet.core.BRCoreChainParams;
import module.nrlwallet.core.BRCoreKey;
import module.nrlwallet.core.BRCoreMasterPubKey;
import module.nrlwallet.core.BRCoreMerkleBlock;
import module.nrlwallet.core.BRCorePeer;
import module.nrlwallet.core.BRCorePeerManager;
import module.nrlwallet.core.BRCoreTransaction;
import module.nrlwallet.core.BRCoreWallet;
import module.nrlwallet.core.BRCoreWalletManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NRLBitcoin extends NRLCoin {
    String url_server = "https://btc.mousebelt.com/api/v1";

    Network network = Bitcoin.MAIN_NET;
    int coinType = 0;
    String seedKey = "Bitcoin seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bSeed;
    String mNemonic;
    String walletAddress1;
    String walletAddress;
    String privateKey;
    WalletAppKit kit;
    JSONArray transactions = new JSONArray();
    BRCoreWalletManager manager;
    BRCoreWallet wallet;
    BRCorePeerManager brCorePeerManager;
    Double balance;
    public boolean syncstatus = false;
    private Executor listenerExecutor = Executors.newSingleThreadExecutor();

    static {
        try {
            System.loadLibrary("core");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }
    private List<Integer> expected;
    private String path;
    private int[] list;
    BRCoreKey brCoreKey;

    public NRLBitcoin(byte[] seed, String mnemonic) {

        super(seed, Bitcoin.MAIN_NET, 0, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");

        bSeed = seed;
        mNemonic = mnemonic;
        this.createWalletAddress();
//        this.createWallet();
    }

    private void createWalletAddress() {
        File chainFile = new File(android.os.Environment.getExternalStorageDirectory(),"btc.spvchain");
        if (chainFile.exists()) {
            chainFile.delete();
        }
        NetworkParameters params = MainNetParams.get();
        kit = new WalletAppKit(params, chainFile, "btc");
        long createTime = System.currentTimeMillis();
        try{
            DeterministicSeed seed = new DeterministicSeed(mNemonic, bSeed, "", createTime);
            kit.restoreWalletFromSeed(seed);
        }catch (UnreadableWalletException e){

        }
        kit.startAsync();
        kit.awaitRunning();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        kit.stopAsync();
//        kit.awaitTerminated();
        walletAddress = kit.wallet().currentReceiveAddress().toBase58();
    }

    private void getTransactionHistory() {
        List<Transaction> history = kit.wallet().getTransactionsByTime();
        int count = kit.wallet().getPendingTransactions().size();
        for(int i = 0; i < history.size(); i++) {
            Transaction transaction = history.get(i);
            String hash = transaction.getHashAsString();
        }
    }
    boolean sentCoins = false;
    public void sendTransaction(long amount, String address, NRLCallback callback) {
        try{
            // Adjust how many coins to send. E.g. the minimum; or everything.
            Coin sendValue = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
            // Coin sendValue = wallet.getBalance().minus(Transaction.DEFAULT_TX_FEE);
            NetworkParameters params = MainNetParams.get();
            Address sendToAdr = Address.fromBase58(params, address);
            SendRequest request = SendRequest.to(sendToAdr, sendValue);

            Wallet.SendResult result = kit.wallet().sendCoins(request);
            result.broadcastComplete.addListener(() -> {
                System.out.println("Coins were sent. Transaction hash: " + result.tx.getHashAsString());
                sentCoins = true;
            }, MoreExecutors.sameThreadExecutor());

            while (!sentCoins) {
                Thread.sleep(100);
            }
            callback.onResponse(result.toString());
        }catch (InsufficientMoneyException e){
            System.err.println(e.getMessage());
            callback.onFailure(e);

        }catch (InterruptedException e){
            System.err.println(e.getMessage());
            callback.onFailure(e);
        }
    }
    private void createWallet_() {
        try {
            ExtendedKey m = ExtendedKey.create(bSeed);
            ExtendedKey ka = module.nrlwallet.com.nrlwalletsdk.Utils.BIP44.getKeyType(m, coinType,0, 0);
            ExtendedKey ck = ka.getChild(coinType);
            walletAddress = ck.getAddress();
            privateKey = WIF.getWif(ck.getMaster());
        } catch (ValidationException e) {
            e.printStackTrace();
        }
//        ExtendedPrivateKey root = ExtendedPrivateKey.fromSeed(bSeed, Bitcoin.MAIN_NET);
//        walletAddress = root
//                .derive("m/44'/0'/0'/0/0")
//                .neuter().p2pkhAddress();

//        ExtendedPrivateKey root = ExtendedPrivateKey.fromSeed(bSeed, Bitcoin.MAIN_NET);
//        String DerivedAddress = root
//                .derive("m/44'/0'/0'/0/0")
//                .neuter().p2pkhAddress();
//        System.out.println(DerivedAddress);
//        try {
//            byte[] tmp = new BRCoreMasterPubKey("tone absurd popular virus fatal possible skirt local head open siren damp".getBytes("UTF-8"), true).serialize();
//
//            BRCoreMasterPubKey brCoreMasterPubKey = new BRCoreMasterPubKey(tmp, true);
//            walletAddress1 = brCoreMasterPubKey.getPubKeyAsCoreKey().address();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
////        BRCoreKey k = new BRCoreKey(tmp, false);
////        BRCoreAddress addr = new BRCoreAddress(DerivedAddress);
////        walletAddress1 = addr.stringify();
//
//        BRCoreMasterPubKey brCoreMasterPubKey = new BRCoreMasterPubKey("tone absurd popular virus fatal possible skirt local head open siren damp".getBytes(), true);
//        String address1 = brCoreMasterPubKey.getPubKeyAsCoreKey().address();

        BRCoreMasterPubKey brCoreMasterPubKey = new BRCoreMasterPubKey(bSeed, true);
        String address = brCoreMasterPubKey.getPubKeyAsCoreKey().address();

        BRCoreWallet.Listener walletListener = getWalletListener();
        brCoreKey = brCoreMasterPubKey.getPubKeyAsCoreKey();
        wallet = createWallet(new BRCoreTransaction[]{}, brCoreMasterPubKey, walletListener);
        createListener();
//        balance = wallet.getBalance();
        walletAddress1 = wallet.getReceiveAddress().stringify();
        privateKey = brCoreMasterPubKey.getPubKeyAsCoreKey().getPrivKey();
    }

    private static BRCoreWallet createWallet (BRCoreTransaction[] transactions,
                                              BRCoreMasterPubKey masterPubKey,
                                              BRCoreWallet.Listener listener) {
        try {
            return new BRCoreWallet(transactions, masterPubKey, listener);
        }
        catch (BRCoreWallet.WalletExecption ex) {
//            asserting (false);
            return null;
        }
    }

    private void createWallet() {
        BRCoreMasterPubKey pubKey = new BRCoreMasterPubKey(mNemonic.getBytes(), true);

        BRCoreChainParams chainParams = BRCoreChainParams.mainnetChainParams;//mainnetChainParams;
        double createTime = System.currentTimeMillis();

        manager = new BRCoreWalletManager(pubKey, chainParams, createTime);
        wallet = manager.getWallet();
        privateKey = pubKey.getPubKeyAsCoreKey().getPrivKey();
        if(wallet.getReceiveAddress().isValid()) {
            walletAddress = wallet.getReceiveAddress().stringify();
        } else {
        }
        manager.getPeerManager().connect();
        manager.syncStarted();
        try {
            Thread.sleep (10 * 1000);
            System.err.println ("Retry");
//            manager.getPeerManager().disconnect();
            manager.getPeerManager().connect();
            manager.getPeerManager().rescan();

//            Thread.sleep(3 * 60 * 1000);
            System.err.println("Times Up - Done");

            Thread.sleep(2 * 1000);
        } catch (InterruptedException ex) {
            System.err.println("Interrupted - Done");
        }
        manager.getPeerManager().disconnect();
        System.gc();
    }

    private void createListener() {
        BRCoreMerkleBlock[] block = new BRCoreMerkleBlock[0];
        BRCorePeerManager.Listener listener = new BRCorePeerManager.Listener() {
            @Override
            public void syncStarted() {

            }

            @Override
            public void syncStopped(String error) {

            }

            @Override
            public void txStatusUpdate() {

            }

            @Override
            public void saveBlocks(boolean replace, BRCoreMerkleBlock[] blocks) {

            }

            @Override
            public void savePeers(boolean replace, BRCorePeer[] peers) {

            }

            @Override
            public boolean networkIsReachable() {
                return false;
            }

            @Override
            public void txPublished(String error) {

            }
        };
        brCorePeerManager = new BRCorePeerManager(
                BRCoreChainParams.testnetChainParams,
                wallet,
                0,
                block,
                new BRCorePeer[0],
                new WrappedExceptionPeerManagerListener(listener)
        );
        brCorePeerManager.connect();
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return walletAddress;
    }

    public void getBalance(NRLCallback callback) {
        this.checkBalance(callback);
    }

    private void checkBalance(NRLCallback callback) {
        String url_getbalance = url_server + "/balance/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result =   (response.body().string());
                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            balance = data.getDouble("balance");
                            String balances = String.valueOf(balance);
                            callback.onResponse(balances);
                            return;
                        }else {
                            callback.onResponse("0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                } else {
                    // Request not successful
                    callback.onResponse("0");
                }
            }
        });
    }

    public void getTransactions(NRLCallback callback) {
        String url_getTransaction = url_server + "/address/txs/" + this.walletAddress;
        new HTTPRequest().run(url_getTransaction, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body =   (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            transactions = data.getJSONArray("result");
                            Double vValue = new Double(0);
                            JSONArray arrTransactions = new JSONArray();

                            for(int i = 0; i < transactions.length(); i++) {
                                JSONObject detail = (JSONObject) transactions.get(i);
                                JSONArray vout = detail.getJSONArray("vout");
                                JSONArray vin = detail.getJSONArray("vin");
                                Double voutVal = new Double(0);

                                Double vinVal = new Double(0);
                                ;
                                for (int j = 0; j < vout.length(); j++) {
                                    JSONObject voutDetail = ((JSONObject) vout.get(j)).getJSONObject("scriptPubKey");
                                    JSONArray addresses = voutDetail.getJSONArray("addresses");
                                    boolean isaddress = false;
                                    for (int k = 0; k < addresses.length(); k++) {
                                        if (addresses.getString(k).equals(walletAddress)) {
                                            isaddress = true;
                                            break;
                                        }
                                    }
                                    if (isaddress) {
                                        voutVal += ((JSONObject) vout.get(j)).getDouble("value");
                                    }
                                }
                                for (int j = 0; j < vin.length(); j++) {
                                    JSONObject vinDetail = (JSONObject) vin.get(j);
                                    JSONObject address = vinDetail.getJSONObject("address");
                                    JSONArray addresses = address.getJSONObject("scriptPubKey").getJSONArray("addresses");
                                    boolean isaddress = false;
                                    for (int k = 0; k < addresses.length(); k++) {
                                        if (addresses.getString(k).equals(walletAddress)) {
                                            isaddress = true;
                                            break;
                                        }
                                    }
                                    if (isaddress) {
                                        vinVal += address.getDouble("value");
                                    }
                                }
                                vValue = -vinVal + voutVal;
                                JSONObject transactionData = new JSONObject();
                                transactionData.put("value", vValue);
                                transactionData.put("txid", detail.getString("txid"));
                                arrTransactions.put(transactionData);
                            }
                            callback.onResponseArray(arrTransactions);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private static Integer[] concat(Integer[] input, int index) {
        final Integer[] integers = Arrays.copyOf(input, input.length + 1);
        integers[input.length] = index;
        return integers;
    }

    public String[] getInputTransaction(int index) {
        BRCoreTransaction[] transactions = manager.getWallet().getTransactions();
        if(transactions.length == 0) return null;
        return transactions[index].getInputAddresses();
    }
    public String[] getOutputTransaction(int index) {
        BRCoreTransaction[] transactions = manager.getWallet().getTransactions();
        if(transactions.length == 0) return null;
        return transactions[index].getOutputAddresses();
    }
    public String[] getAddressOfWallet() {
        BRCoreAddress[] addresses = manager.getWallet().getAllAddresses();
        if(addresses.length == 0) return null;
        String[] arr_address = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            arr_address[i] = addresses[i].stringify();
        }
        return arr_address;
    }

    public void getTransctions(NRLCallback callback) {
        JSONArray jsonArray = new JSONArray();
        BRCoreTransaction[] transactions = manager.getWallet().getTransactions();
        for (BRCoreTransaction transaction : transactions) {
            JSONObject object = new JSONObject();
            try {
                object.put("tx", transaction.toString());
                object.put("size", transaction.getSize());  //long
                object.put("fee", transaction.getStandardFee());    //long
                object.put("input_address", transaction.getInputAddresses());   //[string]
                object.put("output_address", transaction.getOutputAddresses()); //[string]
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
            jsonArray.put(object);
        }
        callback.onResponseArray(jsonArray);
    }

    public String getBalance() {
        return this.balance + "";
    }

    public void createTransaction1(long amount, String address, NRLCallback callback) {
        //String to a private key
//        File chainFile = new File(android.os.Environment.getExternalStorageDirectory(),"btc.spvchain");
        NetworkParameters params = MainNetParams.get();
//        WalletAppKit kit = new WalletAppKit(params, chainFile, "btc");
        Coin value = Coin.valueOf(amount);
        Address to = Address.fromBase58(params, address);
        try{
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);
            System.out.println("coins sent. transaction hash: " + result.tx.getHashAsString());
        } catch (InsufficientMoneyException e) {

        }
    }

    public void createTransaction(long amount, String address, NRLCallback callback) {
        if(address == null){
            callback.onFailure(new Throwable("address null"));
        }
        if(amount <= 0) {
            callback.onFailure(new Throwable("amount is 0"));
            return;
        }
        BRCoreTransaction transaction = manager.getWallet().createTransaction(amount, new BRCoreAddress(address));
        if(transaction == null) {
            callback.onFailure(new Throwable("transaction null"));
        }
        if(!transaction.isSigned()) {
            transaction.sign(brCoreKey, 0);
        }
        brCorePeerManager.publishTransaction(transaction);
        callback.onResponse("success");
    }


    static public class WrappedExceptionPeerManagerListener implements BRCorePeerManager.Listener {
        private BRCorePeerManager.Listener listener;

        public WrappedExceptionPeerManagerListener(BRCorePeerManager.Listener listener) {
            this.listener = listener;
        }
        @Override
        public void syncStarted() {
            try { listener.syncStarted(); }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        @Override
        public void syncStopped(String error) {
            try { listener.syncStopped(error); }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        @Override
        public void txStatusUpdate() {
            try { listener.txStatusUpdate(); }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        @Override
        public void saveBlocks(boolean replace, BRCoreMerkleBlock[] blocks) {
            try { listener.saveBlocks(replace, blocks); }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        @Override
        public void savePeers(boolean replace, BRCorePeer[] peers) {
            try { listener.savePeers(replace, peers); }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        @Override
        public boolean networkIsReachable() {
            try { return listener.networkIsReachable(); }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
                return false;
            }
        }

        @Override
        public void txPublished(String error) {
            try { listener.txPublished(error); }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    private static BRCoreWallet.Listener getWalletListener () {
        return  new BRCoreWallet.Listener() {
            @Override
            public void balanceChanged(long balance) {
                System.out.println(String.format("            balance   : %d", balance));
            }

            @Override
            public void onTxAdded(BRCoreTransaction transaction) {
                System.out.println(String.format("            tx added  : %s",
                        BRCoreKey.encodeHex(transaction.getHash())));

            }

            @Override
            public void onTxUpdated(String hash, int blockHeight, int timeStamp) {
                System.out.println(String.format("            tx updated: %s", hash));
            }

            @Override
            public void onTxDeleted(String hash, int notifyUser, int recommendRescan) {
                System.out.println(String.format("            tx deleted: %s", hash));

            }
        };
    }
}
