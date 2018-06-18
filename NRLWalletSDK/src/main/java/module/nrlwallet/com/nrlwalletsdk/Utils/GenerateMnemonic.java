package module.nrlwallet.com.nrlwalletsdk.Utils;

import java.security.SecureRandom;
import java.util.Arrays;

import static io.github.novacrypto.hashing.Sha256.sha256;

/**
 * Generates mnemonics from entropy.
 */
public final class GenerateMnemonic {

    private final WordList wordList;

    /**
     * Create a generator using the given word list.
     *
     * @param instance
     */
    public GenerateMnemonic(WordList instance) {
        this.wordList = instance;
    }

    public interface Target {
        void append(final CharSequence string);
    }

    /**
     * Create a mnemonic from the word list given the entropy.
     *
     * @param entropyHex 128-256 bits of hex entropy, number of bits must also be divisible by 32
     * @param target     Where to write the mnemonic to
     */
    public void createMnemonic(
            final CharSequence entropyHex,
            final Target target) {
        final int length = entropyHex.length();
        if (length % 2 == 1)
            throw new RuntimeException("Length of hex chars must be divisible by 2");
        final byte[] entropy = new byte[length / 2];
        try {
            for (int i = 0, j = 0; i < length; i += 2, j++) {
                entropy[j] = (byte) (parseHex(entropyHex.charAt(i)) << 4 | parseHex(entropyHex.charAt(i + 1)));
            }
            createMnemonic(target);
        } finally {
            Arrays.fill(entropy, (byte) 0);
        }
    }

    /**
     * Create a mnemonic from the word list given the entropy.
     *
     * @param target  Where to write the mnemonic to
     */
    public void createMnemonic(
            final Target target) {
        byte[] entropy = new byte[Words.TWELVE.byteLength()];

        new SecureRandom().nextBytes(entropy);
        final int[] wordIndexes = wordIndexes(entropy);
        try {
            createMnemonic(wordIndexes, target);
        } finally {
            Arrays.fill(wordIndexes, 0);
        }
    }

    private void createMnemonic(
            final int[] wordIndexes,
            final Target target) {
        final String space = String.valueOf(wordList.getSpace());
        for (int i = 0; i < wordIndexes.length; i++) {
            if (i > 0) target.append(space);
            target.append(wordList.getWord(wordIndexes[i]));
        }
    }

    private static int[] wordIndexes(byte[] entropy) {
        final int ent = entropy.length * 8;
        entropyLengthPreChecks(ent);

        final byte[] entropyWithChecksum = Arrays.copyOf(entropy, entropy.length + 1);
        entropyWithChecksum[entropy.length] = firstByteOfSha256(entropy);

        //checksum length
        final int cs = ent / 32;
        //mnemonic length
        final int ms = (ent + cs) / 11;

        //get the indexes into the word list
        final int[] wordIndexes = new int[ms];
        for (int i = 0, wi = 0; wi < ms; i += 11, wi++) {
            wordIndexes[wi] = next11Bits(entropyWithChecksum, i);
        }
        return wordIndexes;
    }

    static byte firstByteOfSha256(final byte[] entropy) {
        final byte[] hash = sha256(entropy);
        final byte firstByte = hash[0];
        Arrays.fill(hash, (byte) 0);
        return firstByte;
    }

    private static void entropyLengthPreChecks(final int ent) {
        if (ent < 128)
            throw new RuntimeException("Entropy too low, 128-256 bits allowed");
        if (ent > 256)
            throw new RuntimeException("Entropy too high, 128-256 bits allowed");
        if (ent % 32 > 0)
            throw new RuntimeException("Number of entropy bits must be divisible by 32");
    }

    private static int parseHex(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return (c - 'a') + 10;
        if (c >= 'A' && c <= 'F') return (c - 'A') + 10;
        throw new RuntimeException("Invalid hex char '" + c + '\'');
    }
    static int next11Bits(byte[] bytes, int offset) {
        final int skip = offset / 8;
        final int lowerBitsToRemove = (3 * 8 - 11) - (offset % 8);
        return (((int) bytes[skip] & 0xff) << 16 |
                ((int) bytes[skip + 1] & 0xff) << 8 |
                (lowerBitsToRemove < 8
                        ? ((int) bytes[skip + 2] & 0xff)
                        : 0)) >> lowerBitsToRemove & (1 << 11) - 1;
    }

    static void writeNext11(byte[] bytes, int value, int offset) {
        int skip = offset / 8;
        int bitSkip = offset % 8;
        {//byte 0
            byte firstValue = bytes[skip];
            byte toWrite = (byte) (value >> (3 + bitSkip));
            bytes[skip] = (byte) (firstValue | toWrite);
        }

        {//byte 1
            byte valueInByte = bytes[skip + 1];
            final int i = 5 - bitSkip;
            byte toWrite = (byte) (i > 0 ? (value << i) : (value >> -i));
            bytes[skip + 1] = (byte) (valueInByte | toWrite);
        }

        if (bitSkip >= 6) {//byte 2
            byte valueInByte = bytes[skip + 2];
            byte toWrite = (byte) (value << 13 - bitSkip);
            bytes[skip + 2] = (byte) (valueInByte | toWrite);
        }
    }
}