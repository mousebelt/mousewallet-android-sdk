package module.nrlwallet.com.nrlwalletsdk.Stellar;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.OperationType;

import module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.OperationType;

/**
 * Represents <a href="https://www.stellar.org/developers/learn/concepts/list-of-operations.html#inflation" target="_blank">Inflation</a> operation.
 * @see <a href="https://www.stellar.org/developers/learn/concepts/list-of-operations.html" target="_blank">List of Operations</a>
 */
public class InflationOperation extends Operation {
    @Override
    module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody toOperationBody() {
        module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody body = new module.nrlwallet.com.nrlwalletsdk.Stellar.xdr.Operation.OperationBody();
        body.setDiscriminant(OperationType.INFLATION);
        return body;
    }
}
