package module.nrlwallet.com.nrlwalletsdk.Utils;


import module.nrlwallet.com.nrlwalletsdk.Common.ByteUtils;
import module.nrlwallet.com.nrlwalletsdk.Common.Hash;
import module.nrlwallet.com.nrlwalletsdk.Common.Key;

public class WIF {
    public static String getWif(Key k0) {

        String k0priv = printHexBinary(k0.getPrivate());
        String wifraw = "80" + k0priv + "01";
        String cksum = printHexBinary(Hash.sha256(Hash.sha256(parseHexBinary(wifraw))));
        cksum = cksum.substring(0, 8);
        String wif = wifraw + cksum;

        wif = ByteUtils.toBase58(org.bouncycastle.util.encoders.Hex.decode(wif));
        return wif;
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length*2);
        for ( byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
    public static byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if( len%2 != 0 )
            throw new IllegalArgumentException("hexBinary needs to be even-length: "+s);

        byte[] out = new byte[len/2];

        for( int i=0; i<len; i+=2 ) {
            int h = hexToBin(s.charAt(i  ));
            int l = hexToBin(s.charAt(i+1));
            if( h==-1 || l==-1 )
                throw new IllegalArgumentException("contains illegal character for hexBinary: "+s);

            out[i/2] = (byte)(h*16+l);
        }

        return out;
    }

    private static int hexToBin( char ch ) {
        if( '0'<=ch && ch<='9' )    return ch-'0';
        if( 'A'<=ch && ch<='F' )    return ch-'A'+10;
        if( 'a'<=ch && ch<='f' )    return ch-'a'+10;
        return -1;
    }
}
