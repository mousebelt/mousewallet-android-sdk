package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.circuits.hs;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.Router;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.HexDigest;

public class HSDescriptorDirectory {
	
	private final HexDigest descriptorId;
	private final Router directory;
	
	HSDescriptorDirectory(HexDigest descriptorId, Router directory) {
		this.descriptorId = descriptorId;
		this.directory = directory;
	}
	
	Router getDirectory() {
		return directory;
	}
	
	HexDigest getDescriptorId() {
		return descriptorId;
	}
	
	public String toString() {
		return descriptorId + " : " + directory;
	}

}
