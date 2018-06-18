package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

/**
 * Represents MEMO_NONE.
 */
public class MemoNone extends Memo {
  @Override
  module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo toXdr() {
    module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo memo = new module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo();
    memo.setDiscriminant(MemoType.MEMO_NONE);
    return memo;
  }
}
