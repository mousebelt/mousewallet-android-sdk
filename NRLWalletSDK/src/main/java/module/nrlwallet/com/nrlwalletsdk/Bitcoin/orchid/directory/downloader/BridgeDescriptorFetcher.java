package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.downloader;

import java.nio.ByteBuffer;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.RouterDescriptor;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing.DocumentParser;

public class BridgeDescriptorFetcher extends DocumentFetcher<RouterDescriptor>{

	@Override
	String getRequestPath() {
		return "/tor/server/authority";
	}

	@Override
	DocumentParser<RouterDescriptor> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createRouterDescriptorParser(response, true);
	}
}
