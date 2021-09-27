package com.cybertrust.cryptoutils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class Loader {
	
	private static String getKey(String filename) throws IOException {
	    // Read key from file
	    String strKeyPEM = "";
	    BufferedReader br = null;
	    
	    try {
/*		    new BufferedReader(new FileReader(filename));
		    String line;
		    while ((line = br.readLine()) != null) {
		        strKeyPEM += line + "\n";
		    }
		    br.close();
		    return strKeyPEM; */
	    	strKeyPEM = new String(Files.readAllBytes(Paths.get(filename)));
	    	return strKeyPEM;
	    } 
	    catch (Exception e) {
	    	throw(e);
	    }
	    finally {
	    	if (br != null)
	    		br.close();
	    }
	}
	public static RSAPrivateKey getPrivateKeyFromFile(String filename) throws IOException, GeneralSecurityException {
	    String privateKeyPEM = getKey(filename);
	    return getPrivateKeyFromString(privateKeyPEM);
	}

	public static RSAPrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
	    String privateKeyPEM = key;
	    privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
	    privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
	    byte[] encoded = Base64.getMimeDecoder().decode(privateKeyPEM);   
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
	    RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
	    return privKey;
	}

	public static RSAPublicKey getPublicKeyFromFile(String filename) throws IOException, GeneralSecurityException {
	    String publicKeyPEM = getKey(filename);
	    return getPublicKeyFromString(publicKeyPEM);
	}
	
	public static RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
	    String publicKeyPEM = key;
	    publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
	    publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
	    byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
	    return pubKey;
	}
	
	public static RSAPublicKey getPublicKeyFromCertificateFile(String filename) throws IOException, GeneralSecurityException {
		
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(filename);
			CertificateFactory f = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
			return (RSAPublicKey) (certificate.getPublicKey());
		}
		catch (Exception e) {
			throw(e);
		}
		finally {
			fin.close();
		}
	}
	
}

