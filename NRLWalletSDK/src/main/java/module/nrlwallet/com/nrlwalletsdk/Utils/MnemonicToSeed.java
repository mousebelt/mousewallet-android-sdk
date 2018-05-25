package module.nrlwallet.com.nrlwalletsdk.Utils;

import com.google.common.io.BaseEncoding;

import io.github.novacrypto.bip39.SeedCalculator;

public class MnemonicToSeed {

    public MnemonicToSeed() {

    }

    public String calculateSeed(String mnemonic, String passphrase) {
        byte[] seed = new SeedCalculator().calculateSeed(mnemonic, passphrase);
        String str_seed = BaseEncoding.base16().lowerCase().encode(seed);
        return str_seed;
    }
    public byte[] calculateSeedByte(String mnemonic, String passphrase) {
        byte[] seed = new SeedCalculator().calculateSeed(mnemonic, passphrase);
        return seed;
    }
}
