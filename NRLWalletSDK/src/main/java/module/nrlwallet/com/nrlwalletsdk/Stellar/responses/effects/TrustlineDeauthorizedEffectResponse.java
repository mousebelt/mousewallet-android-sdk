package module.nrlwallet.com.nrlwalletsdk.Stellar.responses.effects;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.Server;
import module.nrlwallet.com.nrlwalletsdk.Stellar.requests.EffectsRequestBuilder;

/**
 * Represents trustline_deauthorized effect response.
 * @see <a href="https://www.stellar.org/developers/horizon/reference/resources/effect.html" target="_blank">Effect documentation</a>
 * @see EffectsRequestBuilder
 * @see Server#effects()
 */
public class TrustlineDeauthorizedEffectResponse extends TrustlineAuthorizationResponse {
  TrustlineDeauthorizedEffectResponse(KeyPair trustor, String assetType, String assetCode) {
    super(trustor, assetType, assetCode);
  }
}
