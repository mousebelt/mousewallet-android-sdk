package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.circuits.hs;

public class HSAuthenticationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	HSAuthenticationException(String message) {
		super(message);
	}
	
	HSAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
