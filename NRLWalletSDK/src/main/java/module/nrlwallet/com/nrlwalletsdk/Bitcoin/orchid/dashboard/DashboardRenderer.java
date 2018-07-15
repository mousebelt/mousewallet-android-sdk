package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.dashboard;

import java.io.IOException;
import java.io.PrintWriter;

public interface DashboardRenderer {
	void renderComponent(PrintWriter writer, int flags, Object component) throws IOException;
}
