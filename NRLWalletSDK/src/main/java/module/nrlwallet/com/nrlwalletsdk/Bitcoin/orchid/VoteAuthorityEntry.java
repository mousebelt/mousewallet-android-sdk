package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

import java.util.List;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.HexDigest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.IPv4Address;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.consensus.DirectorySignature;

public interface VoteAuthorityEntry {
	String getNickname();
	HexDigest getIdentity();
	String getHostname();
	IPv4Address getAddress();
	int getDirectoryPort();
	int getRouterPort();
	String getContact();
	HexDigest getVoteDigest();
	List<DirectorySignature> getSignatures();
}
