package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Uint64;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Uint64;

/**
 * Represents MEMO_ID.
 */
public class MemoId extends Memo {
  private long id;

  public MemoId(long id) {
    if (id < 0) {
      throw new IllegalArgumentException("id must be a positive number");
    }
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @Override
  module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo toXdr() {
    module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo memo = new module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo();
    memo.setDiscriminant(MemoType.MEMO_ID);
    Uint64 idXdr = new Uint64();
    idXdr.setUint64(id);
    memo.setId(idXdr);
    return memo;
  }
}
