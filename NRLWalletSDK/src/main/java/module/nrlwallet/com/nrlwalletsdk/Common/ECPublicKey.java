package module.nrlwallet.com.nrlwalletsdk.Common;


import org.bouncycastle.util.Arrays;

public class ECPublicKey implements Key {

    private byte[] pub;
    private boolean compressed;

    public ECPublicKey(byte[] pub, boolean compressed) {
        this.pub = pub;
        this.compressed = compressed;
    }

    @Override
    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public byte[] getAddress() {
        return Hash.keyHash(pub);
    }

    @Override
    public ECPublicKey clone() throws CloneNotSupportedException {
        ECPublicKey c = (ECPublicKey) super.clone();
        c.pub = Arrays.clone(pub);
        return c;
    }

    @Override
    public byte[] getPrivate() {
        return null;
    }

    @Override
    public byte[] getPublic() {
        return Arrays.clone(pub);
    }

    @Override
    public byte[] sign(byte[] data) throws ValidationException {
        throw new ValidationException("Can not sign with public key");
    }

    @Override
    public boolean verify(byte[] hash, byte[] signature) {
        return ECKeyPair.verify(hash, signature, pub);
    }

}