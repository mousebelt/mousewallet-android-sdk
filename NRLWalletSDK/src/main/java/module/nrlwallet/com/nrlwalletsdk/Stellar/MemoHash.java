package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Hash;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

/**
 * Represents MEMO_HASH.
 */
public class MemoHash extends MemoHashAbstract {
  public MemoHash(byte[] bytes) {
    super(bytes);
  }

  public MemoHash(String hexString) {
    super(hexString);
  }

  @Override
  Memo toXdr() {
    Memo memo = new Memo();
    memo.setDiscriminant(MemoType.MEMO_HASH);

    Hash hash = new Hash();
    hash.setHash(bytes);

    memo.setHash(hash);
    return memo;
  }
}
