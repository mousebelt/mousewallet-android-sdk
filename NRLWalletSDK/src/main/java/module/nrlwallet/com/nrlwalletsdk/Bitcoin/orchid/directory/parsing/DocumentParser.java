package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing;


public interface DocumentParser<T> {
	boolean parse(DocumentParsingResultHandler<T> resultHandler);
	DocumentParsingResult<T> parse();
}
