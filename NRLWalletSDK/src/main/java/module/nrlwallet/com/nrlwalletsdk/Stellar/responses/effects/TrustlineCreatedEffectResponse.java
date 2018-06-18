package module.nrlwallet.com.nrlwalletsdk.Stellar.responses.effects;

import module.nrlwallet.com.nrlwalletsdk.Stellar.Server;
import module.nrlwallet.com.nrlwalletsdk.Stellar.requests.EffectsRequestBuilder;

/**
 * Represents trustline_created effect response.
 * @see <a href="https://www.stellar.org/developers/horizon/reference/resources/effect.html" target="_blank">Effect documentation</a>
 * @see EffectsRequestBuilder
 * @see Server#effects()
 */
public class TrustlineCreatedEffectResponse extends TrustlineCUDResponse {
  TrustlineCreatedEffectResponse(String limit, String assetType, String assetCode, String assetIssuer) {
    super(limit, assetType, assetCode, assetIssuer);
  }
}
