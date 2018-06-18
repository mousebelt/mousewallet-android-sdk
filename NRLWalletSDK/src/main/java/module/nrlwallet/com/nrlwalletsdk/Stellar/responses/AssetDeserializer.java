package module.nrlwallet.com.nrlwalletsdk.Stellar.responses;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import module.nrlwallet.com.nrlwalletsdk.Stellar.Asset;
import module.nrlwallet.com.nrlwalletsdk.Stellar.AssetTypeNative;
import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

import java.lang.reflect.Type;

import module.nrlwallet.com.nrlwalletsdk.Stellar.Asset;
import module.nrlwallet.com.nrlwalletsdk.Stellar.AssetTypeNative;
import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

class AssetDeserializer implements JsonDeserializer<Asset> {
  @Override
  public Asset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    String type = json.getAsJsonObject().get("asset_type").getAsString();
    if (type.equals("native")) {
      return new AssetTypeNative();
    } else {
      String code = json.getAsJsonObject().get("asset_code").getAsString();
      String issuer = json.getAsJsonObject().get("asset_issuer").getAsString();
      return Asset.createNonNativeAsset(code, KeyPair.fromAccountId(issuer));
    }
  }
}
