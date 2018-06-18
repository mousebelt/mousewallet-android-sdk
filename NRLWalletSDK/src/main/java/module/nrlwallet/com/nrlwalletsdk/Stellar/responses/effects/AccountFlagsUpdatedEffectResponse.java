package module.nrlwallet.com.nrlwalletsdk.Stellar.responses.effects;

import com.google.gson.annotations.SerializedName;

import module.nrlwallet.com.nrlwalletsdk.Stellar.Server;
import module.nrlwallet.com.nrlwalletsdk.Stellar.requests.EffectsRequestBuilder;

/**
 * Represents account_flags_updated effect response.
 * @see <a href="https://www.stellar.org/developers/horizon/reference/resources/effect.html" target="_blank">Effect documentation</a>
 * @see EffectsRequestBuilder
 * @see Server#effects()
 */
public class AccountFlagsUpdatedEffectResponse extends EffectResponse {
  @SerializedName("auth_required_flag")
  protected final Boolean authRequiredFlag;
  @SerializedName("auth_revokable_flag")
  protected final Boolean authRevokableFlag;

  AccountFlagsUpdatedEffectResponse(Boolean authRequiredFlag, Boolean authRevokableFlag) {
    this.authRequiredFlag = authRequiredFlag;
    this.authRevokableFlag = authRevokableFlag;
  }

  public Boolean getAuthRequiredFlag() {
    return authRequiredFlag;
  }

  public Boolean getAuthRevokableFlag() {
    return authRevokableFlag;
  }
}
