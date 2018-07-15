package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.HexDigest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.IPv4Address;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.Timestamp;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.exitpolicy.ExitPorts;

public interface RouterStatus {
	String getNickname();
	HexDigest getIdentity();
	HexDigest getDescriptorDigest();
	HexDigest getMicrodescriptorDigest();
	Timestamp getPublicationTime();
	IPv4Address getAddress();
	int getRouterPort();
	boolean isDirectory();
	int getDirectoryPort();
	boolean hasFlag(String flag);
	String getVersion();
	boolean hasBandwidth();
	int getEstimatedBandwidth();
	int getMeasuredBandwidth();
	ExitPorts getExitPorts();
}
