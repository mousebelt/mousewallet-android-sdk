package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing;

import java.nio.ByteBuffer;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.ConsensusDocument;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.KeyCertificate;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.RouterDescriptor;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.RouterMicrodescriptor;

public interface DocumentParserFactory {
	DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures);
	
	DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer);

	DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer);

	DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer);
}
