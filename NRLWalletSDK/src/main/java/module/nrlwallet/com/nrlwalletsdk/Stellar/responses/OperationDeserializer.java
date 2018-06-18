package module.nrlwallet.com.nrlwalletsdk.Stellar.responses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.ManageDataOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.OperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.CreateAccountOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.PaymentOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.PathPaymentOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.ManageOfferOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.CreatePassiveOfferOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.SetOptionsOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.ChangeTrustOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.AllowTrustOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.AccountMergeOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.InflationOperationResponse;

import java.lang.reflect.Type;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.AccountMergeOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.AllowTrustOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.ChangeTrustOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.CreateAccountOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.CreatePassiveOfferOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.InflationOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.ManageDataOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.ManageOfferOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.OperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.PathPaymentOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.PaymentOperationResponse;
import module.nrlwallet.com.nrlwalletsdk.Stellar.responses.operations.SetOptionsOperationResponse;

class OperationDeserializer implements JsonDeserializer<OperationResponse> {
  @Override
  public OperationResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    // Create new Gson object with adapters needed in Operation
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(KeyPair.class, new KeyPairTypeAdapter().nullSafe())
            .create();

    int type = json.getAsJsonObject().get("type_i").getAsInt();
    switch (type) {
      case 0:
        return gson.fromJson(json, CreateAccountOperationResponse.class);
      case 1:
        return gson.fromJson(json, PaymentOperationResponse.class);
      case 2:
        return gson.fromJson(json, PathPaymentOperationResponse.class);
      case 3:
        return gson.fromJson(json, ManageOfferOperationResponse.class);
      case 4:
        return gson.fromJson(json, CreatePassiveOfferOperationResponse.class);
      case 5:
        return gson.fromJson(json, SetOptionsOperationResponse.class);
      case 6:
        return gson.fromJson(json, ChangeTrustOperationResponse.class);
      case 7:
        return gson.fromJson(json, AllowTrustOperationResponse.class);
      case 8:
        return gson.fromJson(json, AccountMergeOperationResponse.class);
      case 9:
        return gson.fromJson(json, InflationOperationResponse.class);
      case 10:
        return gson.fromJson(json, ManageDataOperationResponse.class);
      default:
        throw new RuntimeException("Invalid operation type");
    }
  }
}
