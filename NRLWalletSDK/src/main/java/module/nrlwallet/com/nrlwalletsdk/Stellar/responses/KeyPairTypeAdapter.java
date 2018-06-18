package module.nrlwallet.com.nrlwalletsdk.Stellar.responses;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

import java.io.IOException;

import module.nrlwallet.com.nrlwalletsdk.Stellar.KeyPair;

class KeyPairTypeAdapter extends TypeAdapter<KeyPair> {
  @Override
  public void write(JsonWriter out, KeyPair value) throws IOException {
    // Don't need this.
  }

  @Override
  public KeyPair read(JsonReader in) throws IOException {
    return KeyPair.fromAccountId(in.nextString());
  }
}
