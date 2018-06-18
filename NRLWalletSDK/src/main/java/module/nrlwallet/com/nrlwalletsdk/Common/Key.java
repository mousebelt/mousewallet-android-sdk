package module.nrlwallet.com.nrlwalletsdk.Common;

public interface Key extends Cloneable
{
    public byte[] getPrivate ();

    public byte[] getPublic ();

    public byte[] getAddress ();

    public boolean isCompressed ();

    public Key clone () throws CloneNotSupportedException;

    public byte[] sign (byte[] data) throws ValidationException;

    public boolean verify (byte[] data, byte[] signature);
}