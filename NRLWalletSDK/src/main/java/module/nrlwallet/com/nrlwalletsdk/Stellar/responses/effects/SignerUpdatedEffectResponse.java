package module.nrlwallet.com.nrlwalletsdk.Stellar.responses.effects;

import module.nrlwallet.com.nrlwalletsdk.Stellar.Server;
import module.nrlwallet.com.nrlwalletsdk.Stellar.requests.EffectsRequestBuilder;

/**
 * Represents signed_updated effect response.
 * @see <a href="https://www.stellar.org/developers/horizon/reference/resources/effect.html" target="_blank">Effect documentation</a>
 * @see EffectsRequestBuilder
 * @see Server#effects()
 */
public class SignerUpdatedEffectResponse extends SignerEffectResponse {
  SignerUpdatedEffectResponse(Integer weight, String publicKey) {
    super(weight, publicKey);
  }
}
