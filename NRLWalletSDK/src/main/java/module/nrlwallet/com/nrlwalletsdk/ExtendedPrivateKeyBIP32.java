package module.nrlwallet.com.nrlwalletsdk;

import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.toruntime.CheckedExceptionToRuntime;
import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.Ethereum;

import static io.github.novacrypto.toruntime.CheckedExceptionToRuntime.toRuntime;

public class ExtendedPrivateKeyBIP32 {
    public ExtendedPrivateKeyBIP32(){

    }

    //Generate Account Extended Public key
    public String getRootKey(byte[] s_byte, int mode, int Networkmode){
        Network network;
        if(Networkmode == 1){
            network = Ethereum.MAIN_NET;
        }else{
            network = Bitcoin.MAIN_NET;
        }
//        if (mode == 1){
//            network = Bitcoin.MAIN_NET;
//        }
        ExtendedPrivateKey key = ExtendedPrivateKey.fromSeed(s_byte, network);
        String extendedKey = key.extendedBase58();
        return extendedKey;
    }

    private static byte[] getBytes(final String seed) {
        return toRuntime(new CheckedExceptionToRuntime.Func<byte[]>() {
            @Override
            public byte[] run() throws Exception {
                return seed.getBytes("UTF-8");
            }
        });
    }
}
