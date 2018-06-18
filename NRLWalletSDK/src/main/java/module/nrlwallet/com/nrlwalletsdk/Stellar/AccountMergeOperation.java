package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.AccountID;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.OperationType;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.AccountID;
import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.OperationType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents <a href="https://www.stellar.org/developers/learn/concepts/list-of-operations.html#account-merge" target="_blank">AccountMerge</a> operation.
 * @see <a href="https://www.stellar.org/developers/learn/concepts/list-of-operations.html" target="_blank">List of Operations</a>
 */
public class AccountMergeOperation extends Operation {

    private final KeyPair destination;

    private AccountMergeOperation(KeyPair destination) {
        this.destination = checkNotNull(destination, "destination cannot be null");
    }

    /**
     * The account that receives the remaining XLM balance of the source account.
     */
    public KeyPair getDestination() {
        return destination;
    }

    @Override
    module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody toOperationBody() {
        module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody body = new module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody();
        AccountID destination = new AccountID();
        destination.setAccountID(this.destination.getXdrPublicKey());
        body.setDestination(destination);
        body.setDiscriminant(OperationType.ACCOUNT_MERGE);
        return body;
    }

    /**
     * Builds AccountMerge operation.
     * @see AccountMergeOperation
     */
    public static class Builder {
        private final KeyPair destination;

        private KeyPair mSourceAccount;

        Builder(module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody op) {
            destination = KeyPair.fromXdrPublicKey(op.getDestination().getAccountID());
        }

        /**
         * Creates a new AccountMerge builder.
         * @param destination The account that receives the remaining XLM balance of the source account.
         */
        public Builder(KeyPair destination) {
            this.destination = destination;
        }

        /**
         * Set source account of this operation
         * @param sourceAccount Source account
         * @return Builder object so you can chain methods.
         */
        public Builder setSourceAccount(KeyPair sourceAccount) {
            mSourceAccount = sourceAccount;
            return this;
        }

        /**
         * Builds an operation
         */
        public AccountMergeOperation build() {
            AccountMergeOperation operation = new AccountMergeOperation(destination);
            if (mSourceAccount != null) {
                operation.setSourceAccount(mSourceAccount);
            }
            return operation;
        }
    }
}
