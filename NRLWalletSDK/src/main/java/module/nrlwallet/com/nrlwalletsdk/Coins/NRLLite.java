package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import io.github.novacrypto.bip32.Network;
import io.github.novacrypto.bip32.networks.Litecoin;
import io.github.novacrypto.bip44.AddressIndex;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Address;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Coin;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.InsufficientMoneyException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.NetworkParameters;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionBroadcast;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionBroadcaster;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.kits.WalletAppKit;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.SendRequest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.Wallet;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.litecoinj.integrations.LitecoinNetParameters;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Base58Encode;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Secp256k1;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.BRMerkleBlockEntity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.BRPeerEntity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.BRTransactionEntity;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.PaymentItem;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.TxItem;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.listeners.SyncReceiver;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BREventManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRSharedPrefs;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.SyncManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.TxManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security.BRKeyStore;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security.BRSender;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security.PostAuth;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.MerkleBlockDataSource;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.PeerDataSource;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.TransactionDataSource;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRConstants;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRExchange;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.TypesConverter;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.Utils;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRPeerManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRWalletManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.entities.TxMetaData;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.platform.tools.KVStoreManager;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.Utils.Util;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NRLLite extends NRLCoin {
    String url_server = "https://ltc.mousebelt.com/api/v1";
    Context ctx;
    private static Context currentContext;
    Network network = Litecoin.MAIN_NET;
    int coinType = 2;
    String seedKey = "Bitcoin seed";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bSeed;
    String rootKey;
    AddressIndex addressIndex;
    String extendedPrivateKey;
    String extendedPublicKey;
    private String walletAddress;
    String privateKey;
    int count = 0;
    String Wif = "";
    String balance = "0";
    JSONArray transactions = new JSONArray();

    Wallet wallet;
    String str_seed;
    File chainFile;
    WalletAppKit kit;

    byte[] mnemonicbyte;
    byte[] pubKey;
    byte[] authKey;

    NetworkParameters params = LitecoinNetParameters.get();

    static {
        System.loadLibrary("core");
    }

    public NRLLite(byte[] seed, String s_seed, Context context) {

        super(seed, Litecoin.MAIN_NET, 2, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        bSeed = seed;
        str_seed = s_seed;
        ctx = context;
        currentContext = context;
//        boolean success = false;
//        try {
//            success = BRKeyStore.putPhrase(str_seed.getBytes(), currentContext, BRConstants.PUT_PHRASE_NEW_WALLET_REQUEST_CODE);
//        } catch (UserNotAuthenticatedException e) {
//            return ;
//        }
//        if (!success) return ;
        this.createWallet();
//        syncstart();
    }
    public static Context getBreadContext() {
        return currentContext;
    }

    void initWallets() {
        if (!BRWalletManager.getInstance().isCreated()) {
            BRExecutor.getInstance().forBackgroundTasks().execute(new Runnable() {
                @Override
                public void run() {
                    BRWalletManager.getInstance().initWallet(currentContext);
                }
            });
        }
    }

    void newwallet() {
        BRSharedPrefs.putPhraseWroteDown(currentContext, true);
        byte[] bytePhrase = TypesConverter.getNullTerminatedPhrase(str_seed.getBytes());
        byte[] seed = BRWalletManager.getSeedFromPhrase(bytePhrase);
        byte[] authKey = BRWalletManager.getAuthPrivKeyForAPI(seed);
        BRKeyStore.putAuthKey(authKey, currentContext);
        byte[] pubKey = BRWalletManager.getInstance().getMasterPubKey(bytePhrase);
        BRKeyStore.putMasterPublicKey(pubKey, currentContext);
    }

    void createWallet() {
//        byte[] bytePhrase = TypesConverter.getNullTerminatedPhrase(str_seed.getBytes());
//        byte[] seed = BRWalletManager.getSeedFromPhrase(bytePhrase);

        BRWalletManager m = BRWalletManager.getInstance();
        if(m.noWallet(ctx)){
            mnemonicbyte = TypesConverter.getNullTerminatedPhrase(str_seed.getBytes());
            byte[] seed = BRWalletManager.getSeedFromPhrase(mnemonicbyte);


            boolean success = false;
            try {
                success = BRKeyStore.putPhrase(str_seed.getBytes(), currentContext, BRConstants.PUT_PHRASE_NEW_WALLET_REQUEST_CODE);
            } catch (UserNotAuthenticatedException e) {
                return ;
            }
            if (!success) return ;

            authKey = BRWalletManager.getAuthPrivKeyForAPI(seed);
            BRKeyStore.putAuthKey(authKey, currentContext);

            pubKey = BRWalletManager.getInstance().getMasterPubKey(mnemonicbyte);
            BRKeyStore.putMasterPublicKey(pubKey, ctx);
            initWallets();
        }else{
            walletAddress = BRSharedPrefs.getFirstAddress(ctx);
            Log.e("====Wallet Address==", walletAddress);
            initWallets();
            syncstart();
        }
    }
    void initWallet() {
        BRWalletManager m = BRWalletManager.getInstance();
        final BRPeerManager pm = BRPeerManager.getInstance();

        if (!m.isCreated()) {
            List<BRTransactionEntity> transactions = TransactionDataSource.getInstance(ctx).getAllTransactions();
            int transactionsCount = transactions.size();
            if (transactionsCount > 0) {
                m.createTxArrayWithCount(transactionsCount);
                for (BRTransactionEntity entity : transactions) {
                    m.putTransaction(entity.getBuff(), entity.getBlockheight(), entity.getTimestamp());
                }
            }

            byte[] pubkeyEncoded = pubKey;
            if (Utils.isNullOrEmpty(pubkeyEncoded)) {
                return;
            }
            //Save the first address for future check
            m.createWallet(transactionsCount, pubkeyEncoded);
            String firstAddress = BRWalletManager.getFirstAddress(pubkeyEncoded);
            BRSharedPrefs.putFirstAddress(ctx, firstAddress);
            walletAddress = firstAddress;//(pubKey);
            Log.e("====Wallet Address==", walletAddress);
//            long fee = BRSharedPrefs.getFeePerKb(ctx);
//            if (fee == 0) {
//                fee = BRConstants.DEFAULT_FEE_PER_KB;
//                BREventManager.getInstance().pushEvent("wallet.didUseDefaultFeePerKB");
//            }
//            BRWalletManager.getInstance().setFeePerKb(fee, isEconomyFee);
        }

        if (!pm.isCreated()) {
            List<BRMerkleBlockEntity> blocks = MerkleBlockDataSource.getInstance(ctx).getAllMerkleBlocks();
            List<BRPeerEntity> peers = PeerDataSource.getInstance(ctx).getAllPeers();
            final int blocksCount = blocks.size();
            final int peersCount = peers.size();
            if (blocksCount > 0) {
                pm.createBlockArrayWithCount(blocksCount);
                for (BRMerkleBlockEntity entity : blocks) {
                    pm.putBlock(entity.getBuff(), entity.getBlockHeight());
                }
            }
            if (peersCount > 0) {
                pm.createPeerArrayWithCount(peersCount);
                for (BRPeerEntity entity : peers) {
                    pm.putPeer(entity.getAddress(), entity.getPort(), entity.getTimeStamp());
                }
            }

            BRKeyStore.putWalletCreationTime(1531000000, currentContext);

            int walletTime = 1531000000;//BRKeyStore.getWalletCreationTime(ctx);

            pm.create(walletTime, blocksCount, peersCount);

        }
//        BRPeerManager.getInstance().updateFixedPeer(ctx);
//        pm.connect();
//        if (BRSharedPrefs.getStartHeight(ctx) == 0)
//            BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
//                @Override
//                public void run() {
//                    BRSharedPrefs.putStartHeight(ctx, BRPeerManager.getCurrentBlockHeight());
//                }
//            });
        syncstart();
    }

    void syncstart() {
        BRWalletManager.getInstance().addBalanceChangedListener(new BRWalletManager.OnBalanceChanged() {
            @Override
            public void onBalanceChanged(long balance) {

            }
        });
        BRPeerManager.getInstance().addStatusUpdateListener(new BRPeerManager.OnTxStatusUpdate() {
            @Override
            public void onStatusUpdate() {

            }
        });
        BRPeerManager.setOnSyncFinished(new BRPeerManager.OnSyncSucceeded() {
            @Override
            public void onFinished() {
                //put some here
                getBalanceFromBR1();
                getTransactionFromBR1();
//                sendBalanceFromBR("LiXoYvEwWhgAF8tJvPv6C6NNa8GBnQi5np", "200000");
            }
        });

        BRPeerManager.txStatusUpdate();

        if (!BRSharedPrefs.getGreetingsShown(currentContext))
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BRSharedPrefs.putGreetingsShown(currentContext, true);
                }
            }, 1000);
//        onConnectionChanged(true);


        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
//                final double progress = LitecoinPeerManager.syncProgress(BRSharedPrefs.getStartHeight(BreadActivity.this));
////                    Log.e(TAG, "run: " + progress);
//                if (progress < 1 && progress > 0) {
//                    SyncManager.getInstance().startSyncingProgressThread();
//                }
            }
        });
//        sendBalanceFromBR("LiXoYvEwWhgAF8tJvPv6C6NNa8GBnQi5np", "200000");
    }

    public void onConnectionChanged(boolean isConnected) {
        if (isConnected) {
            BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                @Override
                public void run() {
                    final double progress = BRPeerManager.syncProgress(BRSharedPrefs.getStartHeight(currentContext));
//                    Log.e(TAG, "run: " + progress);
                    if (progress < 1 && progress > 0) {
                        SyncManager.getInstance().startSyncingProgressThread();
                    }
                }
            });

        } else {
            SyncManager.getInstance().stopSyncingProgressThread();
        }

    }
    public void getBalanceFromBR1() {
        BigDecimal amount = new BigDecimal(BRSharedPrefs.getCatchedBalance(currentContext));
        balance = amount + "";
        sendBalanceFromBR("LiXoYvEwWhgAF8tJvPv6C6NNa8GBnQi5np", "50000");
    }
    public void getTransactionFromBR1() {
        final TxItem[] arr = BRWalletManager.getInstance().getTransactions();
        JSONArray transactionArray = new JSONArray();
        for(int i = 0; i < arr.length; i++) {
            TxItem item = arr[i];
            JSONObject transactionData = new JSONObject();
            try {
                transactionData.put("value", item.getReceived());
                transactionData.put("txid", item.getTxHashHexReversed());
                transactionArray.put(transactionData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void getBalanceFromBR(NRLCallback callback) {
        BigDecimal amount = new BigDecimal(BRSharedPrefs.getCatchedBalance(currentContext));
        balance = amount + "";
        callback.onResponse(balance);
    }

    public void getTransactionFromBR(NRLCallback callback) {
        final TxItem[] arr = BRWalletManager.getInstance().getTransactions();
        JSONArray transactionArray = new JSONArray();
        for(int i = 0; i < arr.length; i++) {
            TxItem item = arr[i];
            JSONObject transactionData = new JSONObject();
            try {
                transactionData.put("value", item.getReceived());
                transactionData.put("txid", item.getTxHashHexReversed());
                transactionArray.put(transactionData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        callback.onResponseArray(transactionArray);
    }

    public void sendBalanceFromBR(String toAddress, String amount) {
        BigDecimal satoshiAmount = new BigDecimal(amount);
        BigDecimal balance = new BigDecimal(BRSharedPrefs.getCatchedBalance(currentContext));
        if(satoshiAmount.longValue() > balance.longValue()) {
            return;
        }
//        BRSender.getInstance().sendTransaction(currentContext, new PaymentItem(new String[]{toAddress}, null, satoshiAmount.longValue(), null, false, ""));


        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                byte[] tmpTx = BRWalletManager.getInstance().tryTransaction(toAddress, satoshiAmount.longValue());
                if (tmpTx == null) {
                    return;
                }else{
                    byte[] rawSeed;
                    try {
                        rawSeed = BRKeyStore.getPhrase(currentContext, BRConstants.PAYMENT_PROTOCOL_REQUEST_CODE);
                    } catch (UserNotAuthenticatedException e) {
                        return;
                    }
                    if (rawSeed == null || rawSeed.length < 10 || tmpTx == null) {
                        Log.d("", "onPaymentProtocolRequest() returned: rawSeed is malformed: " + Arrays.toString(rawSeed));
                        return;
                    }
                    if (rawSeed.length < 10) return;

                    final byte[] seed = TypesConverter.getNullTerminatedPhrase(rawSeed);
                    byte[] txHash = BRWalletManager.getInstance().publishSerializedTransaction(tmpTx, seed);
                    Log.e("", "onPublishTxAuth: txhash:" + Arrays.toString(txHash));
                    if (Utils.isNullOrEmpty(txHash)) {
                        Log.e("", "onPublishTxAuth: publishSerializedTransaction returned FALSE");
                    } else {
//                        TxMetaData txMetaData = new TxMetaData();
//                        KVStoreManager.getInstance().putTxMetaData(currentContext, txMetaData, txHash);
                        Log.e("TXHash----------", Util.bytesToHex(txHash));
                    }
                }
            }
        });
    }

    public static void setCurrentProgress(double progress) {

    }
    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return walletAddress;
    }

    private void getTransactionCount() {
        String url_getbalance = url_server + "/address/gettransactioncount/" + this.walletAddress;
        new HTTPRequest().run(url_getbalance, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result =   (response.body().string());
                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")) {
                            count = jsonObj.getInt("data");
                        }else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Request not successful
                }
            }
        });
    }
    public void getBalance(NRLCallback callback) {
        this.checkBalance(callback);
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
                            JSONArray transactionArray = new JSONArray();
                            for(int i = 0; i < transactions.length(); i++) {
                                JSONObject detail = (JSONObject) transactions.get(i);
                                JSONArray vout = detail.getJSONArray("vout");
                                JSONArray vin = detail.getJSONArray("vin");
                                Double voutVal = new Double(0);;
                                Double vinVal = new Double(0);;
                                for(int j = 0; j < vout.length(); j++) {
                                    JSONObject voutDetail = ((JSONObject)vout.get(j)).getJSONObject("scriptPubKey");
                                    JSONArray addresses = voutDetail.getJSONArray("addresses");
                                    boolean isaddress = false;
                                    for(int k = 0; k < addresses.length(); k++) {
                                        if(addresses.getString(k).equals(walletAddress)) {
                                            isaddress = true;
                                            break;
                                        }
                                    }
                                    if(isaddress){
                                        voutVal += ((JSONObject)vout.get(j)).getDouble("value");
                                    }
                                }
                                for(int j = 0; j < vin.length(); j++) {
                                    JSONObject vinDetail = (JSONObject)vin.get(j);
                                    JSONObject address = vinDetail.getJSONObject("address");
                                    JSONArray addresses = address.getJSONObject("scriptPubKey").getJSONArray("addresses");
                                    boolean isaddress = false;
                                    for(int k = 0; k < addresses.length(); k++) {
                                        if(addresses.getString(k).equals(walletAddress)) {
                                            isaddress = true;
                                            break;
                                        }
                                    }
                                    if(isaddress){
                                        vinVal += address.getDouble("value");
                                    }
                                }
                                vValue = -vinVal + voutVal;
                                JSONObject transactionData = new JSONObject();
                                transactionData.put("value", vValue);
                                transactionData.put("txid", detail.getString("txid"));
                                transactionArray.put(transactionData);
                            }
                            callback.onResponseArray(transactionArray);
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

    private void generatePubkeyFromPrivatekey(byte[] seed) {
        byte[] publickey = Secp256k1.getPublicKey(seed);
        String aaa = Base58Encode.encode(publickey);
        System.out.println("************----------- Bitcoin public key     : " + aaa);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private static Integer[] concat(Integer[] input, int index) {
        final Integer[] integers = Arrays.copyOf(input, input.length + 1);
        integers[input.length] = index;
        return integers;
    }
    public void checkBalance(NRLCallback callback) {
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
                            balance = data.getString("balance");
                            callback.onResponse(balance);
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

    public void getTransactionsJson(NRLCallback callback) {
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
                            callback.onResponseArray(transactions);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });

    }

    public void createTransaction1(String amount, String address, String memo, long fee, NRLCallback callback) {
//        DumpedPrivateKey dpk = DumpedPrivateKey.fromBase58(params, )
//        ECKey key = dpk.getKey();
        Transaction tx = new Transaction(params);
        Coin coin = Coin.parseCoin(amount);
        Address to = new Address(params, address);
        tx.addOutput(coin, to);
        SendRequest sendRequest = SendRequest.forTx(tx);

//        wallet.signTransaction(sendRequest);

        String url_sendTransaction = url_server + "/sendsignedtransaction/";

        String json = "{\"raw\":\"" + tx.toString() + "\"}";
        RequestBody body = RequestBody.create(HTTPRequest.JSON, json);

        new HTTPRequest().run(url_sendTransaction, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String body =   (response.body().string());
                    try {
                        JSONObject object = new JSONObject(body);
                        String transactionID = object.getString("data");
                        callback.onResponse(transactionID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }

                }else{
                    callback.onResponse("Failed");
                }

            }
        });
    }

    public void createTransaction(String amount, String address, String memo, long fee, NRLCallback callback) {
        Coin value = Coin.parseCoin(amount);
        try {
            Address to = new Address(params, address);
            TransactionBroadcaster transactionBroadcaster = new TransactionBroadcaster() {
                @Override
                public TransactionBroadcast broadcastTransaction(Transaction tx) {
//                    callback.onResponse(tx.toString());
                    callback.onResponse("success " + tx.toString());
                    return null;
                }
            };
            this.wallet.sendCoins(transactionBroadcaster, to, value);
        } catch (InsufficientMoneyException e) {
            System.err.println(e.getMessage());
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
}