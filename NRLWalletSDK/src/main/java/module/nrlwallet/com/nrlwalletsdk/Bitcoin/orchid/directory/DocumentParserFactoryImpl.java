package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory;

import java.nio.ByteBuffer;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.ConsensusDocument;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.KeyCertificate;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.RouterDescriptor;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.RouterMicrodescriptor;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.certificate.KeyCertificateParser;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.consensus.ConsensusDocumentParser;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing.DocumentFieldParser;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing.DocumentParser;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing.DocumentParserFactory;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.router.RouterDescriptorParser;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.router.RouterMicrodescriptorParser;

public class DocumentParserFactoryImpl implements DocumentParserFactory {
	
	public DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer) {
		return new KeyCertificateParser(new DocumentFieldParserImpl(buffer));
	}

	public DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures) {
		return new RouterDescriptorParser(new DocumentFieldParserImpl(buffer), verifySignatures);
	}

	public DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer) {
		buffer.rewind();
		DocumentFieldParser dfp = new DocumentFieldParserImpl(buffer);
		return new RouterMicrodescriptorParser(dfp);
	}

	public DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer) {
		return new ConsensusDocumentParser(new DocumentFieldParserImpl(buffer));
	}
}
