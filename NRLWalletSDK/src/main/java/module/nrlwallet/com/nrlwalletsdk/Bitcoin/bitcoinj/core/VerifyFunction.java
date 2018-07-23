package module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core;

public interface VerifyFunction {
    boolean verify(Block block, boolean throwException) throws VerificationException;
}
