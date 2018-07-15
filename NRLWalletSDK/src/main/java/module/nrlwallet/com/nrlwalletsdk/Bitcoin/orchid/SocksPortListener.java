package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

public interface SocksPortListener {
	void addListeningPort(int port);
	void stop();
}
