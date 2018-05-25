package module.nrlwallet.com.nrlwalletsdk.Utils;

import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.ExtendedPublicKey;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip44.Account;
import io.github.novacrypto.bip44.AddressIndex;
import io.github.novacrypto.bip44.BIP44;
import io.github.novacrypto.bip44.Change;
import io.github.novacrypto.toruntime.CheckedExceptionToRuntime;
import io.github.novacrypto.bip32.Network;
import module.nrlwallet.com.nrlwalletsdk.Network.CoinType;
import module.nrlwallet.com.nrlwalletsdk.Network.Ethereum;
import module.nrlwallet.com.nrlwalletsdk.Network.Neo;

import static io.github.novacrypto.toruntime.CheckedExceptionToRuntime.toRuntime;

public class ExtendedPrivateKeyBIP32 {
    public ExtendedPrivateKeyBIP32(){

    }

    //Generate Account Extended Public key
    public String getRootKey(byte[] s_byte, int Networkmode){
        Network network;
        if(Networkmode == CoinType.ETHEREUM){ // Ethereum
            network = Ethereum.MAIN_NET;
        }else if(Networkmode == CoinType.NEO){ // Neo
            network = Neo.MAIN_NET;
        }else {
            network = Bitcoin.MAIN_NET;
        }
//        if (mode == 1){
//            network = Bitcoin.MAIN_NET;
//        }
        ExtendedPrivateKey key = ExtendedPrivateKey.fromSeed(s_byte, network);

//        getaddress(key);

        String extendedKey = key.extendedBase58();
        return extendedKey;
    }

    private void getaddress(ExtendedPrivateKey root) {
        final Account account = BIP44.
                m().purpose44()
                        .coinType(0)
                        .account(0);
        final ExtendedPublicKey accountKey = root.derive(account, Account.DERIVATION)
                .neuter();

        final Change external = account.external();

        for (int i = 0; i < 20; i++) {
            final AddressIndex derivationPath = external.address(i);
            final ExtendedPublicKey publicKey = accountKey.derive(derivationPath, AddressIndex.DERIVATION_FROM_ACCOUNT);
            System.out.println(derivationPath + " = " + publicKey.p2pkhAddress());
        }
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
