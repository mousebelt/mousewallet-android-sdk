package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.circuits.path;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.Router;

public interface RouterFilter {
	boolean filter(Router router);
}
