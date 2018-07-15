package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.downloader;

import java.nio.ByteBuffer;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.ConsensusDocument;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing.DocumentParser;

public class ConsensusFetcher extends DocumentFetcher<ConsensusDocument>{
	
	private final static String CONSENSUS_BASE_PATH = "/tor/status-vote/current/";
	
	private final boolean useMicrodescriptors;
	
	
	public ConsensusFetcher(boolean useMicrodescriptors) {
		this.useMicrodescriptors = useMicrodescriptors;
	}

	@Override
	String getRequestPath() {
		return CONSENSUS_BASE_PATH + ((useMicrodescriptors) ? 
				("consensus-microdesc") : ("consensus"));
	}

	@Override
	DocumentParser<ConsensusDocument> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createConsensusDocumentParser(response);
	}
}
