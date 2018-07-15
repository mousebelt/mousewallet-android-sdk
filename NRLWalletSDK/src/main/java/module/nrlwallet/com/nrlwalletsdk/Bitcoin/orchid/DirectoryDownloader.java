package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid;

import java.util.List;
import java.util.Set;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.ConsensusDocument.RequiredCertificate;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.data.HexDigest;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.directory.downloader.DirectoryRequestFailedException;

public interface DirectoryDownloader {
	void start(Directory directory);
	void stop();
	
	RouterDescriptor downloadBridgeDescriptor(Router bridge) throws DirectoryRequestFailedException;
	
	ConsensusDocument downloadCurrentConsensus(boolean useMicrodescriptors) throws DirectoryRequestFailedException;
	ConsensusDocument downloadCurrentConsensus(boolean useMicrodescriptors, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<KeyCertificate> downloadKeyCertificates(Set<RequiredCertificate> required) throws DirectoryRequestFailedException;
	List<KeyCertificate> downloadKeyCertificates(Set<RequiredCertificate> required, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<RouterDescriptor> downloadRouterDescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException;
	List<RouterDescriptor> downloadRouterDescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<RouterMicrodescriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException;
	List<RouterMicrodescriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
}
