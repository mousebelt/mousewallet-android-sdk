package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Base58Encode;
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

public class NRLBitcoin extends NRLCoin {

    Network network = Bitcoin.MAIN_NET;
    int coinType = 0;
    String seedKey = "Bitcoin seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bSeed;
    String rootKey;
    AddressIndex addressIndex;
    String extendedPrivateKey;
    String extendedPublicKey;
    String walletAddress1;
    String walletAddress;
    String privateKey;
    BRCoreWalletManager manager;
    BRCoreWallet wallet;
    BRCorePeerManager brCorePeerManager;
    long balance;
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

    public NRLBitcoin(byte[] seed) {

        super(seed, Bitcoin.MAIN_NET, 0, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");

        bSeed = seed;

        this.createWallet();
    }

    private void test() {
        ExtendedPrivateKey root = ExtendedPrivateKey.fromSeed(bSeed, Bitcoin.MAIN_NET);
        String DerivedAddress = root
                .derive("m/44'/0'/0'/0/0")
                .neuter().p2pkhAddress();
        System.out.println(DerivedAddress);
        root.derive("m/44'/0'/0'/0/0");

        Account account = BIP44.m().purpose44()
                        .coinType(0)
                        .account(0);
        final ExtendedPublicKey accountKey = root.derive(account, Account.DERIVATION).neuter();

        final ExtendedPrivateKey privateKey = root.derive("m/44'/0'/0'");
        String AccountExtendedPrivateKeyString = privateKey.extendedBase58();
        System.out.println(AccountExtendedPrivateKeyString);

        String AccountExtendedPublicKeyString = accountKey.extendedBase58();
        System.out.println(AccountExtendedPublicKeyString);

    }
    private void createWallet1() {

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
        balance = wallet.getBalance();
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
        String mnemonic = "tone absurd popular virus fatal possible skirt local head open siren damp";
        BRCoreMasterPubKey pubKey = new BRCoreMasterPubKey("tone absurd popular virus fatal possible skirt local head open siren damp".getBytes(), true);

        BRCoreChainParams chainParams = BRCoreChainParams.mainnetChainParams;
        double createTime = System.currentTimeMillis();

        manager = new BRCoreWalletManager(pubKey, chainParams, createTime);
        wallet = manager.getWallet();
        if(wallet.getReceiveAddress().isValid()) {
            walletAddress = wallet.getReceiveAddress().stringify();
        } else {
        }
        manager.getPeerManager().connect();
        try {
            Thread.sleep (1 * 1000);
            System.err.println ("Retry");
            manager.getPeerManager().disconnect();
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

//        createListener();
//        manager.syncStarted();
        balance = wallet.getBalance();
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
        for (BRCoreTransaction transaction : wallet.getTransactions()) {
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

    public void createTransaction(long amount, String address) {
        if(address == null) return;
        BRCoreTransaction transaction = manager.getWallet().createTransaction(amount, new BRCoreAddress(address));
        if(!transaction.isSigned()) {
            transaction.sign(brCoreKey, 0);
        }
        brCorePeerManager.publishTransaction(transaction);
    }


    static public class WrappedExceptionPeerManagerListener implements BRCorePeerManager.Listener {
        private BRCorePeerManager.Listener listener;

        public WrappedExceptionPeerManagerListener(BRCorePeerManager.Listener listener) {
            this.listener = listener;
        }

        //        private <T> void safeHandler (Supplier<Void> supplier) {
        //            try { supplier.get(); }
        //            catch (Exception ex) {
        //                ex.printStackTrace(System.err);
        //            }
        //        }

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
