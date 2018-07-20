package module.nrlwallet.com.nrlwalletsdk.Coins;

import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Base58;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.Utils.Util;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import neoutils.Neoutils;
import neoutils.RawTransaction;
import neoutils.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class NRLNeo extends NRLCoin {
    String url_server = "https://neo.mousebelt.com/api/v1";

    Network network = Neo.MAIN_NET;
    int coinType = 888;
    String Mnemonic = "";
    String seedKey = "Nist256p1 seed";
    String curve = "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551";
    byte[] bseed;
    String privateKey;
    String walletAddress;
    String Wif;
    Wallet neoWallet;
    JSONArray trnasactions;
    public String balance;
    JSONArray arrUTXO;
    String neoAssetID = "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
    String gasAssetID = "602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7";

    //for payload
    String strError = "";
    double totalAmount = 0;
    byte[] totalpayload;
    byte[] finalPayload;
    byte[] rawTransaction;

    public NRLNeo(byte[] bseed, String mnemonic) {
        super(bseed, Neo.MAIN_NET, 888, "Nist256p1 seed", "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551");
        this.bseed = bseed;
        this.Mnemonic = mnemonic;
        this.init();
    }

    private void test() {
        byte[] aaa = SecureRandom.getSeed(32);
        try {
            neoWallet = Neoutils.generateFromWIF("L59tWNmwh6RsmijTLGmkq8ZKuJyocH41mCFBLVrCbjMwP6tWE8xh");
            byte[] b_privatekey = neoWallet.getPrivateKey();
            privateKey = Neoutils.bytesToHex(b_privatekey);
            byte[] b_publickey = neoWallet.getPublicKey();
//            neoWallet = Neoutils.generateFromPrivateKey(this.privateKey);
            walletAddress = neoWallet.getAddress();
//            Boolean isValid = Neoutils.validateNEOAddress(walletAddress);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Mac sha512_HMAC = null;
        String path = "m/44'/888'/0'/0/0";

        final byte[] b_seedkey = seedKey.getBytes();
        byte[] buf = path.getBytes(Charset.defaultCharset());

        try {
            sha512_HMAC = Mac.getInstance(HMAC_SHA512);

            SecretKeySpec keySpec = new SecretKeySpec(bseed, HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            byte[] mac_data = sha512_HMAC.doFinal(buf);
            byte[] slice = Arrays.copyOfRange(mac_data, 0, 32);
            this.privateKey = Neoutils.bytesToHex(slice);

            neoWallet = Neoutils.generateFromPrivateKey(this.privateKey);
            walletAddress = neoWallet.getAddress();
            String a = neoWallet.getWIF();
//            Boolean isValid = Neoutils.validateNEOAddress(walletAddress);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getAddress() {
        return this.walletAddress;
    }

    public void getBalance(NRLCallback callback) {
        checkBalance(callback);
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
                    String result = (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        String msg = jsonObj.get("msg").toString();
                        if (msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            JSONArray balances = data.getJSONArray("balance");
                            for (int i = 0; i < balances.length(); i++) {
                                JSONObject obj = balances.getJSONObject(i);
                                String ticker = obj.getString("ticker");
                                if (ticker.equals("NEO")) {
                                    balance = obj.getString("value");
                                    callback.onResponse(balance);
                                    return;
                                }
                            }

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
                    String body = (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String msg = jsonObj.get("msg").toString();
                        if (msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            trnasactions = data.getJSONArray("result");
                            callback.onResponseArray(trnasactions);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                    String body = (response.body().string());

                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String msg = jsonObj.get("msg").toString();
                        if (msg.equals("success")) {
                            JSONObject data = jsonObj.getJSONObject("data");
                            trnasactions = data.getJSONArray("result");
                            JSONArray arrTransaction = new JSONArray();
                            for (int i = 0; i < trnasactions.length(); i++) {
                                JSONObject transaction = trnasactions.getJSONObject(i);
                                JSONArray vin = transaction.getJSONArray("vin");
                                JSONArray vout = transaction.getJSONArray("vout");
                                Double transactionVal = new Double(0);
                                Double vinVal = new Double(0);
                                Double voutVal = new Double(0);
                                for (int j = 0; j < vin.length(); j++) {
                                    JSONObject vinObj = vin.getJSONObject(j);
                                    JSONObject addresses = vinObj.getJSONObject("address");
                                    if (addresses.getString("address").equals(walletAddress)) {
                                        vinVal += Double.parseDouble(addresses.getString("value"));
                                    }
                                }
                                for (int j = 0; j < vout.length(); j++) {
                                    JSONObject voutObjArr = vout.getJSONObject(j);
                                    if (voutObjArr.getString("address").equals(walletAddress)) {
                                        voutVal += Double.parseDouble(voutObjArr.getString("value"));
                                    }
                                }
                                transactionVal = vinVal - voutVal;
                                JSONObject transactionData = new JSONObject();
                                transactionData.put("txid", transaction.getString("txid"));
                                transactionData.put("value", transactionVal);
                                arrTransaction.put(transactionData);
                            }
                            callback.onResponseArray(arrTransaction);
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


    private void sendTransaction(String raw, NRLCallback callback) {

        String url_sendTransaction = url_server + "/sendrawtransaction/";
        String json = "{\"hex\":\"" + raw + "\"}";
        System.out.println("--request--" + json);
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
                        if(object.getInt("status") == 200){
                            if(object.getBoolean("data")){
                                callback.onResponse("success");
                            }else{
                                callback.onResponse("Failed");
                            }
                        }else{
                            callback.onResponse("Failed");
                        }
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

    // network string,
    // scriptHash string,
    // wif string,
    // sendingAssetID string,
    // amount float64,
    // remark string,
    // networkFeeAmountInGAS float64
    public void createTransaction(double amount, String address, String memo, double fee, NRLCallback callback) {

//        String toSendAsset = Util.bytesToHex(neoWallet.getHashedSignature());
        getUTXO(amount, address, memo, fee, callback);

    }

    /*
     * Every asset has a list of transaction ouputs representing the total balance
     * For example your total NEO could be represented as a list [tx1, tx2, tx3]
     * and each element contains an individual amount. So your total balance would
     * be represented as SUM([tx1.amount, tx2.amount, tx3.amount]) In order to make
     * a new transaction we will need to find which inputs are necessary in order to
     * satisfy the condition that SUM(Inputs) >= amountToSend
     *
     * We will attempt to get rid of the the smallest inputs first. So we will sort
     * the list of unspents in ascending order, and then keep a running sum until we
     * meet the condition SUM(Inputs) >= amountToSend. If the SUM(Inputs) == amountToSend
     * then we will have one transaction output since no change needs to be returned
     * to the sender. If Sum(Inputs) > amountToSend then we will need two transaction
     * outputs, one that sends the amountToSend to the reciever and one that sends
     * Sum(Inputs) - amountToSend back to the sender, thereby returning the change.
     *
     * Input Payload Structure (where each Transaction Input is 34 bytes ). Let n be the
     * number of input transactions necessary | Inputs.count | Tx1 | Tx2 |....| Txn |
     *
     *
     *                             * Input Data Detailed View *
     * |    1 byte    |         32 bytes         |       2 bytes     | 34 * (n - 2) | 34 bytes |
     * | Inputs.count | TransactionId (Reversed) | Transaction Index | ............ |   Txn    |
     *
     *
     *
     *                                               * Final Payload *
     * | 3 bytes  |    1 + (n * 34) bytes     | 1 byte | 32 bytes |     16 bytes (Int64)     |       32 bytes        |
     * | 0x800000 | Input Data Detailed Above |  0x02  |  assetID | toSendAmount * 100000000 | reciever address Hash |
     *
     *
     * |                    16 bytes (Int64)                    |       32 bytes      |  3 bytes |
     * | (totalAmount * 100000000) - (toSendAmount * 100000000) | sender address Hash | 0x014140 |
     *
     *
     * |    32 bytes    |      34 bytes        |
     * | Signature Data | NeoSigned public key |
     *
     * NEED TO DOUBLE CHECK THE BYTE COUNT HERE
     */

    private void preSendTransaction(double amount, String address, String memo, double fee, NRLCallback callback) {

        String toSendAsset = "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
        String recipientAddress = address;
        if(generateSendTransactionPayload(toSendAsset, amount, recipientAddress)) {
            byte[] aa1 = finalPayload;
            byte[] bb1 = rawTransaction;
            String raw = Neoutils.bytesToHex(aa1);//Util.bytesToHex1(aa1);
            sendTransaction(raw, callback);
        }else {
            callback.onResponse("There is not enough balance");
        }
    }

    private JSONArray getSortedUnspents(String asset, JSONArray utxos) throws Exception{
        JSONArray jsonArray = new JSONArray();
        String checkAssetID = "0x" + neoAssetID;
        String checkAssetGasID = "0x" + gasAssetID;
        if (asset.equals(neoAssetID)){
            for(int i = 0; i < utxos.length(); i++) {
                JSONObject object = utxos.getJSONObject(i);
                if(object.getString("asset").equals(checkAssetID)){
                    jsonArray.put(object);
                }
            }
        }else{
            for(int i = 0; i < utxos.length(); i++) {
                JSONObject object = utxos.getJSONObject(i);
                if(object.getString("asset").equals(checkAssetGasID)){
                    jsonArray.put(object);
                }
            }
        }

        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonValues.add(jsonArray.getJSONObject(i));
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "amount";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) (a.get(KEY_NAME) + "");
                    valB = (String) (b.get(KEY_NAME) + "");
                }
                catch (JSONException e) {
                    //do something
                }

                return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArray.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    private boolean generateSendTransactionPayload(String asset, double amount, String toAddress) {
        getInputsNecessaryToSendAsset(asset, amount, arrUTXO);
        if(strError.equals("")){} else {
            return false;
        }
        byte[] payloadPrefix = new byte[]{(byte)0x80, (byte)0x00};
        rawTransaction = packRawTransactionBytes(payloadPrefix, neoWallet, asset, totalpayload, totalAmount, amount, toAddress);
        String privateKeyHex = Neoutils.bytesToHex(neoWallet.getPrivateKey());//Utils.bytesToHex1(neoWallet.getPrivateKey());
        byte[] signatureData;
        try {
            signatureData = Neoutils.sign(rawTransaction, privateKeyHex);
            finalPayload = concatenatePayloadData(rawTransaction, signatureData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] concatenatePayloadData(byte[] txData, byte[] signatureData) {
        byte[] payload = txData;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] publickey = neoWallet.getPublicKey();
        try {
            outputStream.write(payload);
            outputStream.write(0x01);   //signature number
            outputStream.write(0x41);   //signature struct length
            outputStream.write(0x40);   //signature data length
            outputStream.write(signatureData);  //signature
            outputStream.write(0x23);   //contract data length
            outputStream.write(0x21);   //neosigned publickey
            outputStream.write(publickey);
            outputStream.write(0xac);
            byte[] val = outputStream.toByteArray();
            return val;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payload;
    }

    private void getInputsNecessaryToSendAsset(String asset, double amount, JSONArray jsonArray1) {
        JSONArray jsonArray = new JSONArray();
        try{
            jsonArray = getSortedUnspents(asset, jsonArray1);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(jsonArray == null) {
            strError = "Ther is not balance";
            return;
        }
        double _totalAmount = 0;
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                _totalAmount += object.getDouble("amount");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(_totalAmount < amount){
            strError = "Not enough balance";
            return;
        }

        JSONArray neededForTransaction = new JSONArray();
        double runningAmount = 0;
        int index = 0;
        int count = 0;
        while (runningAmount < amount) {
            try {
                neededForTransaction.put(jsonArray.getJSONObject(index));
                runningAmount = runningAmount + jsonArray.getJSONObject(index).getDouble("amount");
                index = index + 1;
                count = count + 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        byte[] inputData = new byte[]{(byte) index};
        for(int j = 0; j < neededForTransaction.length(); j++) {
            try {
                String txid = neededForTransaction.getJSONObject(j).getString("txid");
                int ind = neededForTransaction.getJSONObject(j).getInt("index");
                txid = Util.removePrefix(txid);
                byte[] data = Neoutils.hexTobytes(txid);
                byte[] revesedBytes = Neoutils.reverseBytes(data);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] byte_ind = ByteBuffer.allocate(2).putShort((short)ind).array();
                outputStream.write(inputData);
                outputStream.write(revesedBytes);
                outputStream.write(byte_ind);
//                outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short)ind).array());
                inputData = outputStream.toByteArray();
//                inputData = System.arraycopy(inputData, 0);
//                inputData = inputData + revesedBytes + ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short)ind).array();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        totalAmount = runningAmount;
        totalpayload = inputData;
        strError = "";
        return;

    }

    //from swift
    private byte[] packRawTransactionBytes(byte[] payloadPrefix, Wallet wallet, String asset, byte[] inputData, double runningAmount, double toSendAmount, String toAddress) {
        byte[] inputDataBytes = inputData;
        BigDecimal toSendAmountDecimal = new BigDecimal(toSendAmount).setScale(1);
        BigDecimal runningAmountDecimal = new BigDecimal(runningAmount);
        boolean needsTwoOutputTransactions = runningAmount != toSendAmount;
        int numberOfAttributes = 0x00;
        byte[] attributesPayload = new byte[0];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(payloadPrefix);
            outputStream.write(numberOfAttributes);
            outputStream.write(inputDataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] payload = outputStream.toByteArray();
        try {
            if(runningAmount == 0){
                ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                outputStream1.write(payload);
                outputStream1.write(0);
                byte[] r1 = outputStream1.toByteArray();
                return r1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(needsTwoOutputTransactions) {
            //Transaction To Reciever
            ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
            try {
                outputStream2.write(payload);
                outputStream2.write(0x02);
                outputStream2.write(Neoutils.reverseBytes(Neoutils.hexTobytes(asset)));
                long amountToSendInMemory = Util.toSafeMemory(toSendAmountDecimal, 8);
//                byte[] byte_amountToSendInMemory = ByteBuffer.allocate(8).putShort((short)amountToSendInMemory).array();
//                outputStream2.write(byte_amountToSendInMemory);
                outputStream2.write(to8BytesArray(amountToSendInMemory));
                //reciever addressHash
                String a1 = hashFromAddress(toAddress);
                byte[] b1 = Neoutils.hexTobytes(a1);
                outputStream2.write(b1);
                //Transaction To Sender
                byte[] b2 = Neoutils.reverseBytes(Neoutils.hexTobytes(asset));
                outputStream2.write(b2);
                long amountToGetBackInMemory = Util.toSafeMemory(runningAmountDecimal, 8) - Util.toSafeMemory(toSendAmountDecimal, 8);
                outputStream2.write(to8BytesArray(amountToGetBackInMemory));
                outputStream2.write(wallet.getHashedSignature());

                byte[] return1 = outputStream2.toByteArray();
                return return1;

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
            try {
                outputStream3.write(payload);
                outputStream3.write(0x01);
                outputStream3.write(Neoutils.reverseBytes(Neoutils.hexTobytes(asset)));
                long amountToSendInMemory = Util.toSafeMemory(toSendAmountDecimal, 8);
                outputStream3.write(to8BytesArray(amountToSendInMemory));

                outputStream3.write(Neoutils.hexTobytes(hashFromAddress(toAddress)));

                byte[] return2 = outputStream3.toByteArray();
                return return2;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return payload;
    }

    private byte[] packRawTransactionBytes1(byte[] payloadPrefix, Wallet wallet, String asset, byte[] inputData, double runningAmount, double toSendAmount, String toAddress) {
        byte[] inputDataBytes = inputData;
        BigDecimal toSendAmountDecimal = new BigDecimal(toSendAmount).setScale(1);
        BigDecimal runningAmountDecimal = new BigDecimal(runningAmount);
        boolean needsTwoOutputTransactions = runningAmountDecimal != toSendAmountDecimal;
        int numberOfAttributes = 0;
        byte[] attributesPayload;


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(payloadPrefix);
            outputStream.write(numberOfAttributes);
            outputStream.write(inputDataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] payload = outputStream.toByteArray();
        try {
            if(runningAmount == 0){
                ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                outputStream1.write(payload);
                outputStream1.write(0);
                byte[] r1 = outputStream1.toByteArray();
                return r1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(needsTwoOutputTransactions) {
            //Transaction To Reciever
            ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
            try {
                outputStream2.write(payload);
                outputStream2.write(0x02);
                outputStream2.write(Neoutils.reverseBytes(Neoutils.hexTobytes(asset)));
                long amountToSendInMemory = Util.toSafeMemory(toSendAmountDecimal, 8);
                outputStream2.write(to8BytesArray(amountToSendInMemory));
                //reciever addressHash
                String a1 = hashFromAddress(toAddress);
                byte[] b1 = Neoutils.hexTobytes(a1);
                outputStream2.write(b1);
                //Transaction To Sender
                byte[] b2 = Neoutils.reverseBytes(Neoutils.hexTobytes(asset));
                outputStream2.write(b2);
                long amountToGetBackInMemory = Util.toSafeMemory(runningAmountDecimal, 8) - Util.toSafeMemory(toSendAmountDecimal, 8);
                outputStream2.write(to8BytesArray(amountToGetBackInMemory));
                outputStream2.write(wallet.getHashedSignature());

                byte[] return1 = outputStream2.toByteArray();
                return return1;

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
            try {
                outputStream3.write(payload);
                outputStream3.write(0x01);
                outputStream3.write(Neoutils.reverseBytes(Neoutils.hexTobytes(asset)));
                long amountToSendInMemory = Util.toSafeMemory(toSendAmountDecimal, 8);
                outputStream3.write(to8BytesArray(amountToSendInMemory));

                outputStream3.write(Neoutils.hexTobytes(hashFromAddress(toAddress)));

                byte[] return2 = outputStream3.toByteArray();
                return return2;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return payload;
    }


    private byte[] to8BytesArray(int value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }
    private byte[] to8BytesArray(long value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
    }

    private String hashFromAddress(String str) {
        byte[] bytes = Base58.decodeChecked(str);
        byte[] shortened = Arrays.copyOfRange(bytes, 0, 21);//need exactly twenty one bytes
        byte[] val = Arrays.copyOfRange(shortened, 1, shortened.length);
        String aaa1 = Neoutils.bytesToHex(val);
        String aaa2 = Util.bytesToHex1(val);
        return aaa1;
    }
//    private void sendNativeAssetTransaction(Wallet wallet, )


















    public void createTransaction1(double amount, String address, String memo, double fee, NRLCallback callback) {
//        this.updateUTXO();

        String str_network = "0xd0";
//        String str_hash =  neoWallet.hashCode() + "";//Neoutils.scriptHashToNEOAddress(address);//
        String str_wif = neoWallet.getWIF();
        String str_hash = Util.bytesToHex(neoWallet.getHashedSignature());
        neoWallet.setAddress(address);
        String str_wif1 = neoWallet.getWIF();

//        neoWallet.setAddress(address);
//        str_wif = neoWallet.getWIF();
//        str_wif = Neoutils.neoAddressToScriptHash(address);


        double d_amount =  amount;//Double.longBitsToDouble(amount);
        double d_fee = 0.025;//Double.longBitsToDouble((long) fee);
        String assetID = "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
        try {
            RawTransaction transaction = Neoutils.mintTokensRawTransactionMobile(str_network, str_hash, str_wif1 , assetID, d_amount, memo, 0.09);
//            callback.onResponse(transaction.getTXID());
            callback.onResponse("success");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e);
        }

    }

    public void getUTXO(double amount, String address, String memo, double fee, NRLCallback callback) {
        String url_getTransaction = url_server + "/address/utxo/" + this.walletAddress;
        new HTTPRequest().run(url_getTransaction, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body =   (response.body().string());
                    arrUTXO = new JSONArray();
                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String msg = jsonObj.get("msg").toString();
                        if(msg.equals("success")){
                            JSONArray data = jsonObj.getJSONArray("data");
                            for(int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                arrUTXO.put(obj);
                            }
                            System.out.println("************----------- UTXO : " + arrUTXO);
                            preSendTransaction(amount, address, memo, fee, callback);
                            return;
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
