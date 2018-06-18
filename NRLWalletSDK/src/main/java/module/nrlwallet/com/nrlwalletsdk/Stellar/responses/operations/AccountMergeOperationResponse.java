package module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations;

import com.google.gson.annotations.SerializedName;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Server;
import module.nrlwallet.com.nrlwalletsdk.Stellar.requests.OperationsRequestBuilder;

/**
 * Represents AccountMerge operation response.
 * @see <a href="https://www.stellar.org/developers/horizon/reference/resources/operation.html" target="_blank">Operation documentation</a>
 * @see OperationsRequestBuilder
 * @see Server#operations()
 */
public class AccountMergeOperationResponse extends OperationResponse {
  @SerializedName("account")
  protected final KeyPair account;
  @SerializedName("into")
  protected final KeyPair into;

  AccountMergeOperationResponse(KeyPair account, KeyPair into) {
    this.account = account;
    this.into = into;
  }

  public KeyPair getAccount() {
    return account;
  }

  public KeyPair getInto() {
    return into;
  }
}
