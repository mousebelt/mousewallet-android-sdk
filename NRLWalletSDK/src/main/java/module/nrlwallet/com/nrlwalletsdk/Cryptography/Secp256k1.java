package module.nrlwallet.com.nrlwalletsdk.Cryptography;

import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.LinkedList;

import javax.crypto.KeyAgreement;

public class Secp256k1 {
//    private static final Logger logger = LoggerFactory.getLogger(Secp256k1.class);
    private static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
    private static final String RANDOM_NUMBER_ALGORITHM_PROVIDER = "SUN";

    /**
     * Generate a random private key that can be used with Secp256k1.
     */
    public static byte[] generatePrivateKey() {
        SecureRandom secureRandom;
        try {
            secureRandom =
                    SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM, RANDOM_NUMBER_ALGORITHM_PROVIDER);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            logger.error(errors.toString());
            secureRandom = new SecureRandom();
        }

        BigInteger privateKeyCheck = BigInteger.ZERO;
        // Bit of magic, move this maybe. This is the max key range.
        BigInteger maxKey =
                new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);

        // Generate the key, skipping as many as desired.
        byte[] privateKeyAttempt = new byte[32];
        secureRandom.nextBytes(privateKeyAttempt);
        privateKeyCheck = new BigInteger(1, privateKeyAttempt);
        while (privateKeyCheck.compareTo(BigInteger.ZERO) == 0
                || privateKeyCheck.compareTo(maxKey) == 1) {
            secureRandom.nextBytes(privateKeyAttempt);
            privateKeyCheck = new BigInteger(1, privateKeyAttempt);
        }

        return privateKeyAttempt;
    }

    /**
     * Converts a private key into its corresponding public key.
     */
    public static byte[] getPublicKey(byte[] privateKey) {
        try {
            ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
            ECPoint pointQ = spec.getG().multiply(new BigInteger(1, privateKey));

            return pointQ.getEncoded(false);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            logger.error(errors.toString());
            return new byte[0];
        }
    }

    /**
     * Sign data using the ECDSA algorithm.
     */
    public static byte[][] signTransaction(byte[] data, byte[] privateKey) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");

            ECDSASigner ecdsaSigner = new ECDSASigner();
            ECDomainParameters domain = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN());
            ECPrivateKeyParameters privateKeyParms =
                    new ECPrivateKeyParameters(new BigInteger(1, privateKey), domain);
            ParametersWithRandom params = new ParametersWithRandom(privateKeyParms);

            ecdsaSigner.init(true, params);

            BigInteger[] sig = ecdsaSigner.generateSignature(data);
            LinkedList<byte[]> sigData = new LinkedList<>();
            byte[] publicKey = getPublicKey(privateKey);
            byte recoveryId = getRecoveryId(sig[0].toByteArray(), sig[1].toByteArray(), data, publicKey);
            for (BigInteger sigChunk : sig) {
                sigData.add(sigChunk.toByteArray());
            }
            sigData.add(new byte[] {recoveryId});
            return sigData.toArray(new byte[][] {});

        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            logger.error(errors.toString());
            return new byte[0][0];
        }
    }

    /**
     * Determine the recovery ID for the given signature and public key.
     *
     * <p>Any signed message can resolve to one of two public keys due to the nature ECDSA. The
     * recovery ID provides information about which one it is, allowing confirmation that the message
     * was signed by a specific key.</p>
     */
    public static byte getRecoveryId(byte[] sigR, byte[] sigS, byte[] message, byte[] publicKey) {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        BigInteger pointN = spec.getN();
        for (int recoveryId = 0; recoveryId < 2; recoveryId++) {
            try {
                BigInteger pointX = new BigInteger(1, sigR);

                X9IntegerConverter x9 = new X9IntegerConverter();
                byte[] compEnc = x9.integerToBytes(pointX, 1 + x9.getByteLength(spec.getCurve()));
                compEnc[0] = (byte) ((recoveryId & 1) == 1 ? 0x03 : 0x02);
                ECPoint pointR = spec.getCurve().decodePoint(compEnc);
                if (!pointR.multiply(pointN).isInfinity()) {
                    continue;
                }

                BigInteger pointE = new BigInteger(1, message);
                BigInteger pointEInv = BigInteger.ZERO.subtract(pointE).mod(pointN);
                BigInteger pointRInv = new BigInteger(1, sigR).modInverse(pointN);
                BigInteger srInv = pointRInv.multiply(new BigInteger(1, sigS)).mod(pointN);
                BigInteger pointEInvRInv = pointRInv.multiply(pointEInv).mod(pointN);
                ECPoint pointQ = ECAlgorithms.sumOfTwoMultiplies(spec.getG(), pointEInvRInv, pointR, srInv);
                byte[] pointQBytes = pointQ.getEncoded(false);
                boolean matchedKeys = true;
                for (int j = 0; j < publicKey.length; j++) {
                    if (pointQBytes[j] != publicKey[j]) {
                        matchedKeys = false;
                        break;
                    }
                }
                if (!matchedKeys) {
                    continue;
                }
                return (byte) (0xFF & recoveryId);
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
//                logger.error(errors.toString());
                continue;
            }
        }

        return (byte) 0xFF;
    }

    /**
     * Recover the public key that corresponds to the private key, which signed this message.
     */
    public static byte[] recoverPublicKey(byte[] sigR, byte[] sigS, byte[] sigV, byte[] message) {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        BigInteger pointN = spec.getN();

        try {
            BigInteger pointX = new BigInteger(1, sigR);

            X9IntegerConverter x9 = new X9IntegerConverter();
            byte[] compEnc = x9.integerToBytes(pointX, 1 + x9.getByteLength(spec.getCurve()));
            compEnc[0] = (byte) ((sigV[0] & 1) == 1 ? 0x03 : 0x02);
            ECPoint pointR = spec.getCurve().decodePoint(compEnc);
            if (!pointR.multiply(pointN).isInfinity()) {
                return new byte[0];
            }

            BigInteger pointE = new BigInteger(1, message);
            BigInteger pointEInv = BigInteger.ZERO.subtract(pointE).mod(pointN);
            BigInteger pointRInv = new BigInteger(1, sigR).modInverse(pointN);
            BigInteger srInv = pointRInv.multiply(new BigInteger(1, sigS)).mod(pointN);
            BigInteger pointEInvRInv = pointRInv.multiply(pointEInv).mod(pointN);
            ECPoint pointQ = ECAlgorithms.sumOfTwoMultiplies(spec.getG(), pointEInvRInv, pointR, srInv);
            byte[] pointQBytes = pointQ.getEncoded(false);
            return pointQBytes;
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            logger.error(errors.toString());
        }

        return new byte[0];
    }

    /**
     * Generate a shared AES key using ECDH.
     */
    public static byte[] generateSharedSecret(byte[] privateKey, byte[] publicKey) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
            ECPublicKeySpec pubKey = new ECPublicKeySpec(spec.getCurve().decodePoint(publicKey), spec);
            KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
            PublicKey otherKey = kf.generatePublic(pubKey);

            ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(1, privateKey), spec);
            PrivateKey myKey = kf.generatePrivate(prvkey);

            KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
            ka.init(myKey);
            ka.doPhase(otherKey, true);
            byte[] password = ka.generateSecret();

            byte[] key = Aes.generateKey(ByteUtilities.toHexString(password), password);

            return key;
        } catch (Exception e) {
//            logger.error(null, e);
            return new byte[0];
        }
    }
}