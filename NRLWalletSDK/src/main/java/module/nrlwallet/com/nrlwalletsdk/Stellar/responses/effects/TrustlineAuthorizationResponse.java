package module.nrlwallet.com.nrlwalletsdk.Stellar.responses.effects;

import com.google.gson.annotations.SerializedName;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

abstract class TrustlineAuthorizationResponse extends EffectResponse {
  @SerializedName("trustor")
  protected final KeyPair trustor;
  @SerializedName("asset_type")
  protected final String assetType;
  @SerializedName("asset_code")
  protected final String assetCode;

  TrustlineAuthorizationResponse(KeyPair trustor, String assetType, String assetCode) {
    this.trustor = trustor;
    this.assetType = assetType;
    this.assetCode = assetCode;
  }

  public KeyPair getTrustor() {
    return trustor;
  }

  public String getAssetType() {
    return assetType;
  }

  public String getAssetCode() {
    return assetCode;
  }
}
