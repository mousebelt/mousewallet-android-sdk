package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.circuits.hs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.DirectoryCircuit;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.InternalCircuit;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.OpenFailedException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.Router;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.Stream;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.StreamConnectFailedException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.TorException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.circuits.CircuitManagerImpl;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.DocumentFieldParserImpl;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.downloader.DirectoryRequestFailedException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.downloader.HttpConnection;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.parsing.DocumentParsingResultHandler;

public class HSDescriptorDownloader {
	private final static Logger logger = Logger.getLogger(HSDescriptorDirectory.class.getName());

	private final HiddenService hiddenService;
	private final CircuitManagerImpl circuitManager;
	private final List<HSDescriptorDirectory> directories;
	
	public HSDescriptorDownloader(HiddenService hiddenService, CircuitManagerImpl circuitManager, List<HSDescriptorDirectory> directories) {
		this.hiddenService = hiddenService;
		this.circuitManager = circuitManager;
		this.directories = directories;
	}

	
	public HSDescriptor downloadDescriptor() {
		for(HSDescriptorDirectory d: directories) {
			HSDescriptor descriptor = downloadDescriptorFrom(d);
			if(descriptor != null) {
				return descriptor;
			}
		}
		// All directories failed
		return null;
	}
	
	private HSDescriptor downloadDescriptorFrom(HSDescriptorDirectory dd) {
		logger.fine("Downloading descriptor from "+ dd.getDirectory());
		
		Stream stream = null;
		try {
			stream = openHSDirectoryStream(dd.getDirectory());
			HttpConnection http = new HttpConnection(stream);
			http.sendGetRequest("/tor/rendezvous2/"+ dd.getDescriptorId().toBase32());
			http.readResponse();
			if(http.getStatusCode() == 200) {
				return readDocument(dd, http.getMessageBody());
			} else {
				logger.fine("HS descriptor download for "+ hiddenService.getOnionAddressForLogging() + " failed with status "+ http.getStatusCode());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch (TimeoutException e) {
			logger.fine("Timeout downloading HS descriptor from "+ dd.getDirectory());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.info("IOException downloading HS descriptor from "+ dd.getDirectory() +" : "+ e);
			return null;
		} catch (OpenFailedException e) {
			logger.info("Failed to open stream to HS directory "+ dd.getDirectory() +" : "+ e.getMessage());
			return null;
		} catch (DirectoryRequestFailedException e) {
			logger.info("Directory request to HS directory "+ dd.getDirectory() + " failed "+ e.getMessage());
			return null;
		} finally {
			if(stream != null) {
				stream.close();
				stream.getCircuit().markForClose();
			}
		}
		
		return null;
		
	}
	
	private Stream openHSDirectoryStream(Router directory) throws TimeoutException, InterruptedException, OpenFailedException {

		final InternalCircuit circuit = circuitManager.getCleanInternalCircuit();
		
		try {
			final DirectoryCircuit dc = circuit.cannibalizeToDirectory(directory);
			return dc.openDirectoryStream(10000, true);
		} catch (StreamConnectFailedException e) {
			circuit.markForClose();
			throw new OpenFailedException("Failed to open directory stream");
		} catch (TorException e) {
			circuit.markForClose();
			throw new OpenFailedException("Failed to extend circuit to HS directory: "+ e.getMessage());
		}
	}

	private HSDescriptor readDocument(HSDescriptorDirectory dd, ByteBuffer body) {
		DocumentFieldParserImpl fieldParser = new DocumentFieldParserImpl(body);
		HSDescriptorParser parser = new HSDescriptorParser(hiddenService, fieldParser, hiddenService.getAuthenticationCookie());
		DescriptorParseResult result = new DescriptorParseResult(dd);
		parser.parse(result);
		return result.getDescriptor();
	}
	
	private static class DescriptorParseResult implements DocumentParsingResultHandler<HSDescriptor> {
		HSDescriptorDirectory dd;
		HSDescriptor descriptor;
		
		public DescriptorParseResult(HSDescriptorDirectory dd) {
			this.dd = dd;
		}
	
		HSDescriptor getDescriptor() {
			return descriptor;
		}
		public void documentParsed(HSDescriptor document) {
			this.descriptor = document;
		}

		public void documentInvalid(HSDescriptor document, String message) {
			logger.info("Invalid HS descriptor document received from "+ dd.getDirectory() + " for descriptor "+ dd.getDescriptorId());
		}

		public void parsingError(String message) {
			logger.info("Failed to parse HS descriptor document received from "+ dd.getDirectory() + " for descriptor "+ dd.getDescriptorId() + " : " + message);
		}
	}
}