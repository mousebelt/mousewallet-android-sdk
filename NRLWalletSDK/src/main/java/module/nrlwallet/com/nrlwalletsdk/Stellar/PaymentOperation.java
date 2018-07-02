package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.AccountID;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Int64;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.OperationType;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.PaymentOp;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.AccountID;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Int64;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.OperationType;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.PaymentOp;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents <a href="https://www.stellar.org/developers/learn/concepts/list-of-operations.html#payment" target="_blank">Payment</a> operation.
 * @see <a href="https://www.stellar.org/developers/learn/concepts/list-of-operations.html" target="_blank">List of Operations</a>
 */
public class PaymentOperation extends Operation {

  private final KeyPair destination;
  private final Asset asset;
  private final String amount;

  private PaymentOperation(KeyPair destination, Asset asset, String amount) {
    this.destination = checkNotNull(destination, "destination cannot be null");
    this.asset = checkNotNull(asset, "asset cannot be null");
    this.amount = checkNotNull(amount, "amount cannot be null");
  }

  /**
   * Account that receives the payment.
   */
  public KeyPair getDestination() {
    return destination;
  }

  /**
   * Asset to send to the destination account.
   */
  public Asset getAsset() {
    return asset;
  }

  /**
   * Amount of the asset to send.
   */
  public String getAmount() {
    return amount;
  }

  @Override
  module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody toOperationBody() {
    PaymentOp op = new PaymentOp();

    // destination
    AccountID destination = new AccountID();
    destination.setAccountID(this.destination.getXdrPublicKey());
    op.setDestination(destination);
    // asset
    op.setAsset(asset.toXdr());
    // amount
    Int64 amount = new Int64();
    amount.setInt64(Operation.toXdrAmount(this.amount));
    op.setAmount(amount);

    module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody body = new module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody();
    body.setDiscriminant(OperationType.PAYMENT);
    body.setPaymentOp(op);
    return body;
  }

  /**
   * Builds Payment operation.
   * @see PathPaymentOperation
   */
  public static class Builder {
    private final KeyPair destination;
    private final Asset asset;
    private final String amount;

    private KeyPair mSourceAccount;

    /**
     * Construct a new PaymentOperation builder from a PaymentOp XDR.
     * @param op {@link PaymentOp}
     */
    Builder(PaymentOp op) {
      destination = KeyPair.fromXdrPublicKey(op.getDestination().getAccountID());
      asset = Asset.fromXdr(op.getAsset());
      amount = Operation.fromXdrAmount(op.getAmount().getInt64().longValue());
    }

    /**
     * Creates a new PaymentOperation builder.
     * @param destination The destination keypair (uses only the public key).
     * @param asset The asset to send.
     * @param amount The amount to send in lumens.
     * @throws ArithmeticException when amount has more than 7 decimal places.
     */
    public Builder(KeyPair destination, Asset asset, long amount) {
      this.destination = destination;
      this.asset = asset;
      this.amount = String.valueOf(amount);
    }

    /**
     * Sets the source account for this operation.
     * @param account The operation's source account.
     * @return Builder object so you can chain methods.
     */
    public Builder setSourceAccount(KeyPair account) {
      mSourceAccount = account;
      return this;
    }

    /**
     * Builds an operation
     */
    public PaymentOperation build() {
      PaymentOperation operation = new PaymentOperation(destination, asset, amount);
      if (mSourceAccount != null) {
        operation.setSourceAccount(mSourceAccount);
      }
      return operation;
    }
  }
}
