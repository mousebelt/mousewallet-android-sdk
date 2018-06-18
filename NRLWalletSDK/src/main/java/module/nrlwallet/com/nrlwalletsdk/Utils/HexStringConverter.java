package module.nrlwallet.com.nrlwalletsdk.Utils;

import java.io.UnsupportedEncodingException;

public class HexStringConverter
{
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static HexStringConverter hexStringConverter = null;

    private HexStringConverter()
    {}

    public static HexStringConverter getHexStringConverterInstance()
    {
        if (hexStringConverter==null) hexStringConverter = new HexStringConverter();
        return hexStringConverter;
    }

    public String stringToHex(String input) throws UnsupportedEncodingException
    {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes());
    }

    public String hexToString(String txtInHex)
    {
        byte [] txtInByte = new byte [txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2)
        {
            txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }

    public String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

}