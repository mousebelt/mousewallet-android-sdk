package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.exitpolicy;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.IPv4Address;

public interface ExitTarget {
	boolean isAddressTarget();
	IPv4Address getAddress();
	String getHostname();
	int getPort();
}
