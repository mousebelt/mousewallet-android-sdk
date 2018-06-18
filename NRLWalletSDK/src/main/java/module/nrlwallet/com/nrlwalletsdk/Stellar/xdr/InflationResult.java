// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package module.nrlwallet.com.nrlwalletsdk.Stellar.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  union InflationResult switch (InflationResultCode code)
//  {
//  case INFLATION_SUCCESS:
//      InflationPayout payouts<>;
//  default:
//      void;
//  };

//  ===========================================================================
public class InflationResult  {
  public InflationResult () {}
  InflationResultCode code;
  public InflationResultCode getDiscriminant() {
    return this.code;
  }
  public void setDiscriminant(InflationResultCode value) {
    this.code = value;
  }
  private InflationPayout[] payouts;
  public InflationPayout[] getPayouts() {
    return this.payouts;
  }
  public void setPayouts(InflationPayout[] value) {
    this.payouts = value;
  }
  public static void encode(XdrDataOutputStream stream, InflationResult encodedInflationResult) throws IOException {
  stream.writeInt(encodedInflationResult.getDiscriminant().getValue());
  switch (encodedInflationResult.getDiscriminant()) {
  case INFLATION_SUCCESS:
  int payoutssize = encodedInflationResult.getPayouts().length;
  stream.writeInt(payoutssize);
  for (int i = 0; i < payoutssize; i++) {
    InflationPayout.encode(stream, encodedInflationResult.payouts[i]);
  }
  break;
  default:
  break;
  }
  }
  public static InflationResult decode(XdrDataInputStream stream) throws IOException {
  InflationResult decodedInflationResult = new InflationResult();
  InflationResultCode discriminant = InflationResultCode.decode(stream);
  decodedInflationResult.setDiscriminant(discriminant);
  switch (decodedInflationResult.getDiscriminant()) {
  case INFLATION_SUCCESS:
  int payoutssize = stream.readInt();
  decodedInflationResult.payouts = new InflationPayout[payoutssize];
  for (int i = 0; i < payoutssize; i++) {
    decodedInflationResult.payouts[i] = InflationPayout.decode(stream);
  }
  break;
  default:
  break;
  }
    return decodedInflationResult;
  }
}
