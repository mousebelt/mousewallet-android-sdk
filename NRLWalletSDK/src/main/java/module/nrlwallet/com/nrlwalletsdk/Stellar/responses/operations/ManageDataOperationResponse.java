package module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations;

import com.google.gson.annotations.SerializedName;

import module.nrlwallet.com.nrlwalletsdk.Stellar.Server;
import module.nrlwallet.com.nrlwalletsdk.Stellar.requests.OperationsRequestBuilder;

/**
 * Represents ManageDataoperation response.
 * @see <a href="https://www.stellar.org/developers/horizon/reference/resources/operation.html" target="_blank">Operation documentation</a>
 * @see OperationsRequestBuilder
 * @see Server#operations()
 */
public class ManageDataOperationResponse extends OperationResponse {
  @SerializedName("name")
  protected final String name;
  @SerializedName("value")
  protected final String value;

  ManageDataOperationResponse(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
