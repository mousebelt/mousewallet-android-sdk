package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

import java.util.Set;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.crypto.TorPublicKey;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.HexDigest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.IPv4Address;

public interface Router {

	String getNickname();
	String getCountryCode();
	IPv4Address getAddress();
	int getOnionPort();
	int getDirectoryPort();
	TorPublicKey getIdentityKey();
	HexDigest getIdentityHash();
	boolean isDescriptorDownloadable();

	String getVersion();
	Descriptor getCurrentDescriptor();
	HexDigest getDescriptorDigest();
	HexDigest getMicrodescriptorDigest();

	TorPublicKey getOnionKey();
	byte[] getNTorOnionKey();
	
	boolean hasBandwidth();
	int getEstimatedBandwidth();
	int getMeasuredBandwidth();

	Set<String> getFamilyMembers();
	int getAverageBandwidth();
	int getBurstBandwidth();
	int getObservedBandwidth();
	boolean isHibernating();
	boolean isRunning();
	boolean isValid();
	boolean isBadExit();
	boolean isPossibleGuard();
	boolean isExit();
	boolean isFast();
	boolean isStable();
	boolean isHSDirectory();
	boolean exitPolicyAccepts(IPv4Address address, int port);
	boolean exitPolicyAccepts(int port);
}
