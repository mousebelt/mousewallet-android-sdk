package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

import java.nio.charset.Charset;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.MemoType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents MEMO_TEXT.
 */
public class MemoText extends Memo {
  private String text;

  public MemoText(String text) {
    this.text = checkNotNull(text, "text cannot be null");

    int length = text.getBytes((Charset.forName("UTF-8"))).length;
    if (length > 28) {
      throw new MemoTooLongException("text must be <= 28 bytes. length=" + String.valueOf(length));
    }
  }

  public String getText() {
    return text;
  }

  @Override
  module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo toXdr() {
    module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo memo = new module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Memo();
    memo.setDiscriminant(MemoType.MEMO_TEXT);
    memo.setText(text);
    return memo;
  }
}
