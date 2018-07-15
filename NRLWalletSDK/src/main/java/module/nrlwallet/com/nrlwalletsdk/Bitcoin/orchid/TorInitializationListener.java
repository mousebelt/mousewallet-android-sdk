package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

public interface TorInitializationListener {
	void initializationProgress(String message, int percent);
	void initializationCompleted();
}
