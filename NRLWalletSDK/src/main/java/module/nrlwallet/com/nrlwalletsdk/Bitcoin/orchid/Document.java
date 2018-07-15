package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

import java.nio.ByteBuffer;

public interface Document {
	ByteBuffer getRawDocumentBytes();
	String getRawDocumentData();
	boolean isValidDocument();
}
