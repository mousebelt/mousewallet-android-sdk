package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.github.novacrypto.bip32.Network;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip44.AddressIndex;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Base58Encode;
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
    String walletAddress;
    String privateKey;
    String Mnemonic;
    BRCoreWalletManager manager;
    BRCoreWallet wallet;
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

    public NRLBitcoin(byte[] seed, String mnemonic) {

        super(seed, Bitcoin.MAIN_NET, 0, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        bSeed = seed;
        Mnemonic = mnemonic;

        this.createWallet1();
    }
    private void createWallet1() {
        BRCoreMasterPubKey brCoreMasterPubKey = new BRCoreMasterPubKey(bSeed, true);
        BRCoreWallet.Listener walletListener = getWalletListener();
        wallet = createWallet(new BRCoreTransaction[]{}, brCoreMasterPubKey, walletListener);
        createListener();
        balance = wallet.getBalance();
        walletAddress = wallet.getReceiveAddress().stringify();
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
        BRCoreMasterPubKey brCoreMasterPubKey = new BRCoreMasterPubKey(bSeed, true);
        BRCoreChainParams chainParams = BRCoreChainParams.testnetChainParams;
        double createTime = System.currentTimeMillis();
        manager = new BRCoreWalletManager(brCoreMasterPubKey, chainParams, createTime);
        createListener();
//        manager.syncStarted();
        BRCoreWallet wallet = manager.getWallet();
        walletAddress = brCoreMasterPubKey.getPubKeyAsCoreKey().address();
        byte[] pubKey = new BRCoreMasterPubKey(bSeed, true).serialize();
        extendedPublicKey = Base58Encode.encode(pubKey);
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
        BRCorePeerManager brCorePeerManager = new BRCorePeerManager(
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

    public JSONArray getTransctions() {
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
            }
            jsonArray.put(object);
        }
        return jsonArray;
    }

    public String getBalance() {
        return this.balance + "";
    }

    public void createTransaction(long amount, String address) {
        if(address == null) return;
        BRCoreTransaction transaction = manager.getWallet().createTransaction(amount, new BRCoreAddress(address));
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
