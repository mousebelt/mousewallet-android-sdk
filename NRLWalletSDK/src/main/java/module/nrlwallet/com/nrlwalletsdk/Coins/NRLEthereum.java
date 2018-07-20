package module.nrlwallet.com.nrlwalletsdk.Coins;

import io.ethmobile.ethdroid.sha3.Keccak;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.ChildNumber;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.DeterministicKey;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.crypto.HDUtils;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicKeyChain;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.DeterministicSeed;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.wallet.UnreadableWalletException;

import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.Ethereum;
import module.nrlwallet.com.nrlwalletsdk.Utils.HTTPRequest;
import module.nrlwallet.com.nrlwalletsdk.Utils.Keccak256Helper;
import module.nrlwallet.com.nrlwalletsdk.Utils.Util;
import module.nrlwallet.com.nrlwalletsdk.abstracts.NRLCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NRLEthereum extends NRLCoin {
    String url_server = "https://eth.mousebelt.com/api/v1";
    Network network = Ethereum.MAIN_NET;
    int coinType = 60;
    String seedKey = "Bitcoin seed";
    String Mnemonic = "";
    String curve = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141";
    byte[] bSeed;
    String walletAddress;
    int count = 0;
    String privateKey;
    String privateKey_origin;
    JSONArray transactions = new JSONArray();
    Credentials credentials;

    public NRLEthereum(byte[] seed, String strMnemonic) {
        super(seed, Ethereum.MAIN_NET, 60, "Bitcoin seed", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141");
        bSeed = seed;
        Mnemonic = strMnemonic;
        this.createAddress();
    }

    private void createAddress() {
        DeterministicSeed seed = null;
        try {
            Long creationtime = new Date().getTime();
            seed = new DeterministicSeed(Mnemonic, null, "", creationtime);
            DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
            List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
            DeterministicKey key = chain.getKeyByPath(keyPath, true);
            BigInteger privKey = key.getPrivKey();

// Web3j
            credentials = Credentials.create(privKey.toString(16));
            this.privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
            privateKey_origin = "0x" + credentials.getEcKeyPair().getPrivateKey().toString(16);

            this.walletAddress = credentials.getAddress();
            this.getTransactionCount();
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAddress() {
        return this.walletAddress;
    }

    @Override
    public String getPrivateKey() {
        return this.privateKey;
    }

    public void getBalance(NRLCallback callback) {
        this.checkBalance(callback);
    }

    public void getTransactionCount() {
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
                            JSONArray balances = data.getJSONArray("balances");
                            callback.onResponseArray(balances);
                            return;
//                            for(int i = 0; i < balances.length(); i++) {
//                                JSONObject obj = balances.getJSONObject(i);
//                                String ticker = obj.getString("symbol");
//                                if(ticker.equals("ETH")){
//                                    balance = obj.getString("balance");
//                                    callback.onResponse(balance);
//                                    return;
//                                }
//                            }
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
                            JSONArray arrTransactions = new JSONArray();
                            for(int i = 0; i < transactions.length(); i++ ) {
                                JSONObject object = transactions.getJSONObject(i);
                                JSONObject retunValue = new JSONObject();
                                retunValue.put("txid", object.getString("blockHash"));
                                if(object.getString("from").equals(walletAddress)){
                                    retunValue.put("value", "-" + object.getString("value"));
                                }else {
                                    retunValue.put("value", "+" + object.getString("value"));
                                }
                                arrTransactions.put(retunValue);
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

    //amount : send amount
    //address : receive address
    //memo :
    //fee
    //tokenID : ERC20 token id
    //tokenAddress : ERC20 token address
    //value : d
    public void createTransaction(String amount, String address, String memo, double fee, String tokenID, NRLCallback callback) {
        BigInteger nonce = BigInteger.valueOf(this.count);
        BigInteger gas_price = BigInteger.valueOf((long) fee);
        String amount_data = "0x" + amount;
        String strAddress = stringTo64Symbols(address);
        BigInteger send_amount = BigInteger.valueOf(Long.valueOf(amount));
        // gas price 1,000,000,000 ===10e9
        // amount 1ETH = 1,000,000,000,000,000,000 WEI ==== 10e18
        //nonce, <gas price>, <gas limit>, <toAddress>, <value>
//        Transaction transaction = null;
        RawTransaction rawTransaction = null;
        if(memo.equals("ETH")){
            rawTransaction = RawTransaction.createEtherTransaction(nonce, gas_price, BigInteger.valueOf(21000), address, send_amount);
        } else {    //ERC20 Token
            String address_to = stringTo64Symbols(address);
            String value = stringTo64Symbols(amount);
            String strvalue = decToHex(Integer.parseInt(value));
            strvalue = stringTo64Symbols(strvalue);

            String MethodID = "0xa9059cbb";
            String ToAddress= address_to;
            String ToAmount = strvalue;
            String sendData = MethodID + ToAddress + ToAmount;
            rawTransaction = RawTransaction.createTransaction(nonce, gas_price, BigInteger.valueOf(65000), tokenID, BigInteger.ZERO, sendData);
        }


        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String str_Raw = "";
        str_Raw = bytesToHex(signedMessage);
        System.out.println("************----------- : " + str_Raw);
        String url_sendTransaction = url_server + "/sendsignedtransaction/";

        String json = "{\"raw\":\"" + str_Raw + "\"}";
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
                        count++;
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

    public char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public  String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public String toHex(String arg) {
        return String.format("%08x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }
    public String inputData(String contractId, String address, String value) throws Exception {
        if (!WalletUtils.isValidAddress(address)) {
            throw new Exception("address error");
        }
        String strContract = "0x" + contractId;
        String strAddress = stringTo64Symbols(address);
        String strvalue = decToHex(Integer.parseInt(value));
//        String strValue = stringValueFormat(value, 16);
        strvalue = stringTo64Symbols(strvalue);
        return strContract + strAddress + strvalue;
    }
    public String stringValueFormat(String value, int radix) {
        BigDecimal bigDecimal = new BigDecimal(value);
        long divide = 1000000000000000000L;
        BigDecimal bd = new BigDecimal(divide);
        BigDecimal doubleWithStringValue = bd.multiply(bigDecimal);
        return doubleWithStringValue.toBigInteger().toString(radix);
    }
    public String stringTo64Symbols(String line) {
        if (line.charAt(0) == '0' && line.charAt(1) == 'x') {
            StringBuilder buffer = new StringBuilder(line);
            buffer.deleteCharAt(0);
            buffer.deleteCharAt(0);
            line = buffer.toString();
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("0000000000000000000000000000000000000000000000000000000000000000");

        for (int i = 0; i < line.length(); i++) {
            buffer.setCharAt(64 - i - 1, line.charAt(line.length() - i - 1));
        }
        return buffer.toString();

    }
    private static final int sizeOfIntInHalfBytes = 8;
    private static final int numberOfBitsInAHalfByte = 4;
    private static final int halfByte = 0x0F;
    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    public String decToHex(int dec) {
        StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
        hexBuilder.setLength(sizeOfIntInHalfBytes);
        for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i)
        {
            int j = dec & halfByte;
            hexBuilder.setCharAt(i, hexDigits[j]);
            dec >>= numberOfBitsInAHalfByte;
        }
        return hexBuilder.toString();
    }
}