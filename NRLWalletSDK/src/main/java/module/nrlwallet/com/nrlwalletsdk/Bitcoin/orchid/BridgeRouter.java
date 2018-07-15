package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;


import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.HexDigest;

public interface BridgeRouter extends Router {
	void setIdentity(HexDigest identity);
	void setDescriptor(RouterDescriptor descriptor);
}
