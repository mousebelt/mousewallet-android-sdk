// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package module.nrlwallet.com.nrlwalletsdk.Stellar.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  struct Hello
//  {
//      uint32 ledgerVersion;
//      uint32 overlayVersion;
//      uint32 overlayMinVersion;
//      Hash networkID;
//      string versionStr<100>;
//      int listeningPort;
//      NodeID peerID;
//      AuthCert cert;
//      uint256 nonce;
//  };

//  ===========================================================================
public class Hello  {
  public Hello () {}
  private Uint32 ledgerVersion;
  public Uint32 getLedgerVersion() {
    return this.ledgerVersion;
  }
  public void setLedgerVersion(Uint32 value) {
    this.ledgerVersion = value;
  }
  private Uint32 overlayVersion;
  public Uint32 getOverlayVersion() {
    return this.overlayVersion;
  }
  public void setOverlayVersion(Uint32 value) {
    this.overlayVersion = value;
  }
  private Uint32 overlayMinVersion;
  public Uint32 getOverlayMinVersion() {
    return this.overlayMinVersion;
  }
  public void setOverlayMinVersion(Uint32 value) {
    this.overlayMinVersion = value;
  }
  private Hash networkID;
  public Hash getNetworkID() {
    return this.networkID;
  }
  public void setNetworkID(Hash value) {
    this.networkID = value;
  }
  private String versionStr;
  public String getVersionStr() {
    return this.versionStr;
  }
  public void setVersionStr(String value) {
    this.versionStr = value;
  }
  private Integer listeningPort;
  public Integer getListeningPort() {
    return this.listeningPort;
  }
  public void setListeningPort(Integer value) {
    this.listeningPort = value;
  }
  private NodeID peerID;
  public NodeID getPeerID() {
    return this.peerID;
  }
  public void setPeerID(NodeID value) {
    this.peerID = value;
  }
  private AuthCert cert;
  public AuthCert getCert() {
    return this.cert;
  }
  public void setCert(AuthCert value) {
    this.cert = value;
  }
  private Uint256 nonce;
  public Uint256 getNonce() {
    return this.nonce;
  }
  public void setNonce(Uint256 value) {
    this.nonce = value;
  }
  public static void encode(XdrDataOutputStream stream, Hello encodedHello) throws IOException{
    Uint32.encode(stream, encodedHello.ledgerVersion);
    Uint32.encode(stream, encodedHello.overlayVersion);
    Uint32.encode(stream, encodedHello.overlayMinVersion);
    Hash.encode(stream, encodedHello.networkID);
    stream.writeString(encodedHello.versionStr);
    stream.writeInt(encodedHello.listeningPort);
    NodeID.encode(stream, encodedHello.peerID);
    AuthCert.encode(stream, encodedHello.cert);
    Uint256.encode(stream, encodedHello.nonce);
  }
  public static Hello decode(XdrDataInputStream stream) throws IOException {
    Hello decodedHello = new Hello();
    decodedHello.ledgerVersion = Uint32.decode(stream);
    decodedHello.overlayVersion = Uint32.decode(stream);
    decodedHello.overlayMinVersion = Uint32.decode(stream);
    decodedHello.networkID = Hash.decode(stream);
    decodedHello.versionStr = stream.readString();
    decodedHello.listeningPort = stream.readInt();
    decodedHello.peerID = NodeID.decode(stream);
    decodedHello.cert = AuthCert.decode(stream);
    decodedHello.nonce = Uint256.decode(stream);
    return decodedHello;
  }
}
