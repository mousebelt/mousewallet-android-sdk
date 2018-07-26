package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

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
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.SendRequest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.Wallet;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.litecoinj.integrations.LitecoinNetParameters;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Base58Encode;
import module.nrlwallet.com.nrlwalletsdk.Cryptography.Secp256k1;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.presenter.entities.TxItem;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.BRSharedPrefs;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.manager.SyncManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.security.BRKeyStore;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.sqlite.BRSQLiteHelper;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.threads.BRExecutor;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.BRConstants;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.TypesConverter;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.tools.util.Utils;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRPeerManager;
import module.nrlwallet.com.nrlwalletsdk.Litecoin.breadwallet.wallet.BRWalletManager;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.Utils.Util;
import module.nrlwallet.com.nrlwalletsdk.abstracts.LTCCallback;
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

    byte[] mnemonicbyte;
    byte[] pubKey;
    byte[] authKey;
    boolean isSyncing = false;
    LTCCallback ltcCallback;
    boolean isExist = true;

    NetworkParameters params = LitecoinNetParameters.get();

    static {
        System.loadLibrary("core");
    }

    public NRLLite(byte[] seed, String mnemonic, Context context, LTCCallback callback, boolean isExist) {

        super(seed, Litecoin.MAIN_NET, 2, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        bSeed = seed;
        str_seed = mnemonic;
        ctx = context;
        currentContext = context;
        this.ltcCallback = callback;
        this.isExist = isExist;
        if(!isExist) {
            BRSharedPrefs.clearAllPrefs(context);
            BRSQLiteHelper dbHelper = BRSQLiteHelper.getInstance(context);
            dbHelper.formatSQLite(dbHelper.getWritableDatabase());
        }
        this.createWallet();
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

    @TargetApi(Build.VERSION_CODES.M)
    void createWallet() {
        isSyncing = true;
        BRWalletManager m = BRWalletManager.getInstance();
        if(m.noWallet(ctx) || !isExist){
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
            syncstart();
        }else{
            walletAddress = BRSharedPrefs.getFirstAddress(ctx);
            Log.e("====Wallet Address==", walletAddress);
            initWallets();
            syncstart();
        }
    }
    void syncstart() {
        BRWalletManager.getInstance().addBalanceChangedListener(new BRWalletManager.OnBalanceChanged() {
            @Override
            public void onBalanceChanged(long balance) {
                getWalletData();
            }
        });
        BRPeerManager.getInstance().addStatusUpdateListener(new BRPeerManager.OnTxStatusUpdate() {
            @Override
            public void onStatusUpdate() {
                int startHeight = BRSharedPrefs.getStartHeight(currentContext);
                int lastcount =  BRSharedPrefs.getLastBlockHeight(currentContext);
                long progress = (startHeight - lastcount);
            }
        });
        BRPeerManager.setOnSyncFinished(new BRPeerManager.OnSyncSucceeded() {
            @Override
            public void onFinished() {
                //put some here
                isSyncing = false;
                getWalletData();
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
            }
        });
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

    public boolean SyncingNow() {
        return isSyncing;
    }

    public void getWalletData() {
        long amount = BRWalletManager.getInstance().nativeBalance();
        balance = amount + "";
        walletAddress = BRSharedPrefs.getFirstAddress(currentContext);
        final TxItem[] arr = BRWalletManager.getInstance().getTransactions();
        JSONArray transactionArray = new JSONArray();
        for(int i = 0; i < arr.length; i++) {
            TxItem item = arr[i];
            JSONObject transactionData = new JSONObject();
            try {
                long received = item.getReceived();
                byte[]tmp = item.getTxHash();
                transactionData.put("txid", Util.bytesToHex(tmp));
                if(received == 0){
                    transactionData.put("received", false);
                    transactionData.put("value", item.getSent());
                } else {
                    transactionData.put("received", true);
                    transactionData.put("value", item.getReceived());
                }

                transactionArray.put(transactionData);
            } catch (JSONException e) {
                e.printStackTrace();
                ltcCallback.onFailed("failed");
            }
        }
        JSONObject object = new JSONObject();
        try {
            object.put("address", walletAddress);
            object.put("balance", balance);
            object.put("history", transactionArray);
        } catch (JSONException e) {
            e.printStackTrace();
            ltcCallback.onFailed("failed");
        }
        ltcCallback.onResponse(object);
//        sendBalanceFromBR("LcZHGWTW4ZFA2SxpV1niNhYR8ovTKfQxM2", "500000", null);
    }
    public void getBalanceFromBR(NRLCallback callback) {

        long amount = BRWalletManager.getInstance().getBalance(currentContext);//new BigDecimal(BRSharedPrefs.getCatchedBalance(currentContext));
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

    public void sendBalanceFromBR(String toAddress, String amount, NRLCallback callback) {
        BigDecimal satoshiAmount = new BigDecimal(amount);
        BigDecimal balance = new BigDecimal(BRWalletManager.getInstance().getBalance(currentContext));
        if(satoshiAmount.longValue() > balance.longValue()) {
            callback.onResponse("Balance is not enough");
            return;
        }

        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                byte[] tmpTx = BRWalletManager.getInstance().tryTransaction(toAddress, satoshiAmount.longValue());
                if (tmpTx == null) {
                    callback.onResponse("Send transaction failed");
                    return;
                }else{
                    byte[] rawSeed;
                    try {
                        rawSeed = BRKeyStore.getPhrase(currentContext, BRConstants.PAYMENT_PROTOCOL_REQUEST_CODE);
                    } catch (@SuppressLint("NewApi") UserNotAuthenticatedException e) {
                        callback.onResponse("Signing failed");
                        return;
                    }
                    if (rawSeed == null || rawSeed.length < 10 || tmpTx == null) {
                        Log.d("", "onPaymentProtocolRequest() returned: rawSeed is malformed: " + Arrays.toString(rawSeed));
                        callback.onResponse("Signing failed");
                        return;
                    }
//                    if (rawSeed.length < 10) return;

                    final byte[] seed = TypesConverter.getNullTerminatedPhrase(rawSeed);
                    byte[] txHash = BRWalletManager.getInstance().publishSerializedTransaction(tmpTx, seed);
                    Log.e("", "onPublishTxAuth: txhash:" + Arrays.toString(txHash));
                    if (Utils.isNullOrEmpty(txHash)) {
                        Log.e("", "onPublishTxAuth: publishSerializedTransaction returned FALSE");
                    } else {
//                        TxMetaData txMetaData = new TxMetaData();
//                        KVStoreManager.getInstance().putTxMetaData(currentContext, txMetaData, txHash);
                        Log.e("TXHash----------", Util.bytesToHex(txHash));
                        callback.onResponse(Util.bytesToHex(txHash));
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