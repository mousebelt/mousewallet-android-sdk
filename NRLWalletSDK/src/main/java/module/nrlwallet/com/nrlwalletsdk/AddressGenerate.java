package module.nrlwallet.com.nrlwalletsdk;

import java.util.Arrays;
import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.ExtendedPublicKey;
import io.github.novacrypto.bip44.AddressIndex;
import io.github.novacrypto.bip44.BIP44;
import module.nrlwallet.com.nrlwalletsdk.Network.Ethereum;

public class AddressGenerate {
    public AddressIndex addressIndex;
    public AddressGenerate(int _cointype, int _account, int _address) {
        addressIndex = BIP44.m()
                .purpose44()
                .coinType(_cointype)
                .account(_account)
                .external()
                .address(_address);
    }

    public String[] getBIP32ExtendedKeyArray(byte[] seed) {
        ExtendedPrivateKey privateKey = ExtendedPrivateKey.fromSeed(seed, Ethereum.MAIN_NET);

        ExtendedPrivateKey child = privateKey.derive("m/44'/60'/0'/0");
        ExtendedPublicKey childPub = child.neuter();
        String str1 = child.extendedBase58();
        String str2 = childPub.extendedBase58();
        String str3 = childPub.p2pkhAddress();
        String str4 = childPub.p2shAddress();
        String[] keyArray = {str1, str2, str3, str4};

        return keyArray;

    }

    private static Integer[] concat(Integer[] input, int index) {
        final Integer[] integers = Arrays.copyOf(input, input.length + 1);
        integers[input.length] = index;
        return integers;
    }

    public int getAddressIndex(){
        return addressIndex.getValue();
    }

}
