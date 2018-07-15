package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.downloader;

import java.nio.ByteBuffer;
import java.util.Set;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.KeyCertificate;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.ConsensusDocument.RequiredCertificate;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing.DocumentParser;

public class CertificateFetcher extends DocumentFetcher<KeyCertificate>{

	private final Set<RequiredCertificate> requiredCertificates;
	
	public CertificateFetcher(Set<RequiredCertificate> requiredCertificates) {
		this.requiredCertificates = requiredCertificates;
	}

	@Override
	String getRequestPath() {
		return "/tor/keys/fp-sk/"+ getRequiredCertificatesRequestString();
	}

	private String getRequiredCertificatesRequestString() {
		final StringBuilder sb = new StringBuilder();
		for(RequiredCertificate rc: requiredCertificates) {
			if(sb.length() > 0) {
				sb.append("+");
			}
			sb.append(rc.getAuthorityIdentity().toString());
			sb.append("-");
			sb.append(rc.getSigningKey().toString());
		}
		return sb.toString();
	}

	@Override
	DocumentParser<KeyCertificate> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createKeyCertificateParser(response);
	}
}
