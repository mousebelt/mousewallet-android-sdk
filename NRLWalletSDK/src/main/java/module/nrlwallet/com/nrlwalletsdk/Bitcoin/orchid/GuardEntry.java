package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

import java.util.Date;

public interface GuardEntry {
	boolean isAdded();
	void markAsDown();
	void clearDownSince();
	String getNickname();
	String getIdentity();
	String getVersion();
	Date getCreatedTime();
	Date getDownSince();
	Date getLastConnectAttempt();
	Date getUnlistedSince();
	boolean testCurrentlyUsable();
	Router getRouterForEntry();
}
