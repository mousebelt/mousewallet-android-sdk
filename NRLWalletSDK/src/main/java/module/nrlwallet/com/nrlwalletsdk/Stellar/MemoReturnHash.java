package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Hash;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

/**
 * Represents MEMO_RETURN.
 */
public class MemoReturnHash extends MemoHashAbstract {
  public MemoReturnHash(byte[] bytes) {
    super(bytes);
  }

  public MemoReturnHash(String hexString) {
    super(hexString);
  }

  @Override
  Memo toXdr() {
    Memo memo = new Memo();
    memo.setDiscriminant(MemoType.MEMO_RETURN);

    Hash hash = new Hash();
    hash.setHash(bytes);

    memo.setHash(hash);
    return memo;
  }
}
