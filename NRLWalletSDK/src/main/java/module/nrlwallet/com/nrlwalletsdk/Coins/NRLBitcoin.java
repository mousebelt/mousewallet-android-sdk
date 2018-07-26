package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.webkit.DownloadListener;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Address;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.BlockChain;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Coin;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Context;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.ECKey;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.InsufficientMoneyException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.NetworkParameters;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.PeerGroup;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionBroadcast;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionBroadcaster;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.listeners.DownloadProgressTracker;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.kits.WalletAppKit;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.net.discovery.DnsDiscovery;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.params.MainNetParams;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.store.BlockStore;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.store.BlockStoreException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.store.SPVBlockStore;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicSeed;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.Protos;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.SendRequest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.UnreadableWalletException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import io.github.novacrypto.bip32.Network;
import io.github.novacrypto.bip32.networks.Bitcoin;
import jersey.repackaged.com.google.common.util.concurrent.MoreExecutors;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.WalletProtobufSerializer;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
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
    String walletAddress;
    String privateKey;
    WalletAppKit kit;
    Wallet wallet;
    JSONArray transactions = new JSONArray();
    Double balance;
    NetworkParameters params = MainNetParams.get();
    int originalBlocksLeft = -1;

    //for wallet kit
    File walletFile;
    PeerGroup peerGroup;
    BlockChain blockChain;
    BlockStore blockStore;

    static {
        try {
            System.loadLibrary("core");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public NRLBitcoin(byte[] seed, String mnemonic) {

        super(seed, Bitcoin.MAIN_NET, 0, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");

        bSeed = seed;
        mNemonic = mnemonic;
//        this.getWallet();
        initWalletService();
    }
    void initWalletService() {
        walletFile = new File(android.os.Environment.getExternalStorageDirectory(), "btc.wallet");
        long createTime = 1529131310L;
        try {
            DeterministicSeed deterministicSeed = new DeterministicSeed(mNemonic, null, "", createTime);
            kit = new WalletAppKit(params, walletFile, "blc"){
                @Override
                protected void onSetupCompleted() {
                    super.onSetupCompleted();
                    final long startTime = System.currentTimeMillis();
                    try{
                        wallet = kit.wallet();
                        peerGroup = kit.peerGroup();
                        blockChain = kit.chain();
                        blockStore = kit.store();

                        Coin coinBalance = wallet.getBalance();
                    } catch (Exception e) {

                    }
                }
            };
            kit.restoreWalletFromSeed(deterministicSeed);
            kit.setBlockingStartup(false);
            kit.startAsync();
            kit.awaitRunning();
        }catch (UnreadableWalletException e){
            e.printStackTrace();
        }
    }

    void setChainCheckpoints() {

    }

    void loadWallet() {
        long createTime = 1529131310L;
        try {
            DeterministicSeed deterministicSeed = new DeterministicSeed(mNemonic, null, "", createTime);
            kit.restoreWalletFromSeed(deterministicSeed);
            kit = new WalletAppKit(params, new File("."), "mousebelt") {
                @Override
                protected void onSetupCompleted() {
                    // This is called in a background thread after startAndWait is called, as setting up various objects
                    // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                    // on the main thread.
                    if (wallet().getKeyChainGroupSize() < 1)
                        wallet().importKey(new ECKey());
                }
            };

            kit.startAsync();
            kit.awaitRunning();
        }catch (UnreadableWalletException e){
            e.printStackTrace();
        }
    }

    void getWallet() {
        long createTime = 1529131310L;//System.currentTimeMillis() - 3600*24*30*1000;//1529126900000
        try {
            DeterministicSeed deterministicSeed = new DeterministicSeed(mNemonic, null, "", createTime);
            File chainFile = new File(android.os.Environment.getExternalStorageDirectory(), "btc");
            if (chainFile.exists()) {
//                chainFile.delete();
            }

            kit = new WalletAppKit(params, chainFile, "mousebelt");
            kit.restoreWalletFromSeed(deterministicSeed);
            kit.startAsync();
            kit.awaitRunning();
            kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
                @Override
                public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                    // Runs in the dedicated "user thread".
                }
            });
        }catch (UnreadableWalletException e){
            e.printStackTrace();
        }
    }

    void createWallet() {
//        NetworkParameters params = MainNetParams.get();
        Long creationtime = new Date().getTime() - 3600*24*30;

        long createTime = 1529131310L;//System.currentTimeMillis() - 3600*24*30*1000;//1529126900000
        try{
            DeterministicSeed deterministicSeed = new DeterministicSeed(mNemonic, null, "", createTime);
            wallet = Wallet.fromSeed(params, deterministicSeed);
            walletAddress = wallet.currentReceiveAddress().toBase58();
            wallet.clearTransactions(0);
            File chainFile = new File(android.os.Environment.getExternalStorageDirectory(),"btc.spvchain");
            if(chainFile.exists()){
                chainFile.delete();
            }

            SPVBlockStore chainStore = new SPVBlockStore(params, chainFile);
            BlockChain chain = new BlockChain(params, chainStore);
            PeerGroup peerGroup = new PeerGroup(params, chain);
            peerGroup.addPeerDiscovery(new DnsDiscovery(params));

            // Now we need to hook the wallet up to the blockchain and the peers. This registers event listeners that notify our wallet about new transactions.
            chain.addWallet(wallet);
            peerGroup.addWallet(wallet);

            DownloadProgressTracker bListener = new DownloadProgressTracker() {
                @Override
                public void doneDownload() {
                    System.out.println("blockchain downloaded");
                }

//                @Override
//                public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
////                    double pct = 100.0 - (100.0 * (blocksLeft / (double) originalBlocksLeft));
//
//                }
            };

            // Now we re-download the blockchain. This replays the chain into the wallet. Once this is completed our wallet should know of all its transactions and print the correct balance.
            peerGroup.start();
            peerGroup.startBlockChainDownload(bListener);

            bListener.await();

//            wallet.allowSpendingUnconfirmedTransactions();

            // Print a debug message with the details about the wallet. The correct balance should now be displayed.
            System.out.println(wallet.toString());

            // shutting down again
            peerGroup.stop();
            Thread.sleep(1000);
            wallet.saveToFile(chainFile);
            Thread.sleep(1000);

        }catch (UnreadableWalletException e){
            e.printStackTrace();
        }catch (BlockStoreException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean sentCoins = false;


    public void setTransaction(long amount, String address, NRLCallback callback) {

        NetworkParameters params = MainNetParams.get();
        Coin value = Coin.valueOf(amount);

        try {
            Address to = Address.fromBase58(params, address);
//            SendRequest sendRequest = SendRequest.to(to, value);

            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);
            callback.onResponse(result.tx.getHashAsString());
            System.out.println("coins sent. transaction hash: " + result.tx.getHashAsString());
        } catch (InsufficientMoneyException e) {
            System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
            System.out.println("Send money to: " + kit.wallet().currentReceiveAddress().toString());
            callback.onFailure(e);
        } catch (Wallet.DustySendRequested e){
            System.err.println(e.getMessage());
            callback.onFailure(e);

        } catch (Wallet.CouldNotAdjustDownwards e) {
            System.err.println(e.getMessage());
            callback.onFailure(e);

        } catch (Wallet.ExceededMaxTransactionSize e) {
            System.err.println(e.getMessage());
            callback.onFailure(e);

        } catch (Wallet.MultipleOpReturnRequested e) {
            System.err.println(e.getMessage());
            callback.onFailure(e);

        }
    }
    public void sendTransaction1(long amount, String address, NRLCallback callback) {
        try{
            // Adjust how many coins to send. E.g. the minimum; or everything.
            Coin sendAmount = Coin.valueOf(amount);
//            Coin sendValue = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
            // Coin sendValue = wallet.getBalance().minus(Transaction.DEFAULT_TX_FEE);
            NetworkParameters params = MainNetParams.get();
            Address sendToAdr = Address.fromBase58(params, address);
            SendRequest request = SendRequest.to(sendToAdr, sendAmount);

            Wallet.SendResult result = wallet.sendCoins(request);
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

    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return kit.wallet().currentReceiveAddress().toBase58();
    }

    public String getBalance() {
        String balance1 = kit.wallet().getBalance().value + "";
        return balance1;
    }

    public void getBalance(NRLCallback callback) {
        String balance1 = kit.wallet().getBalance().value + "";
        callback.onResponse(balance1);
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

}
