package com.cybertrust.tms.cryptoutils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;


public class Loader {
	
	public static String getKey(String filename) throws IOException {
	    // Read key from file
	    String strKeyPEM = "";
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = br.readLine()) != null) {
	        strKeyPEM += line + "\n";
	    }
	    br.close();
	    return strKeyPEM;
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
	    publicKeyPEM = publicKeyPEM.replace("\n", "");
	    byte[] encoded = Base64.getDecoder().decode(publicKeyPEM.getBytes("UTF-8"));
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
	    return pubKey;
	    
	}
	
	public static X509Certificate getCertificateFromString(String cert) throws IOException, GeneralSecurityException {
		
		String publicCertPEM = cert;
	    publicCertPEM = publicCertPEM.replace("-----BEGIN CERTIFICATE-----\n", "");
	    publicCertPEM = publicCertPEM.replace("-----END CERTIFICATE-----", "");
	    publicCertPEM = publicCertPEM.replace("\n", "");
	    byte[] encoded = Base64.getDecoder().decode(publicCertPEM.getBytes("UTF-8"));
	    CertificateFactory cf = CertificateFactory.getInstance("X509");
	    X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(encoded));
		return certificate;
		
	}
	
	public static PemObject getPemFromString (String cert) throws IOException {
		
		StringReader reader = new StringReader(cert);
		PemReader pr = new PemReader(reader);
		PemObject certificate = pr.readPemObject();
		pr.close();
		return certificate;
		
	}
	
	public static RSAPublicKey getPublicKeyFromCertificateFile(String filename) throws IOException, GeneralSecurityException {
		FileInputStream fin = new FileInputStream(filename);
		CertificateFactory f = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
		return (RSAPublicKey) (certificate.getPublicKey());
	}
	
	public static X509Certificate getCertFromCertFile(String filename) throws IOException, GeneralSecurityException {
		FileInputStream fin = new FileInputStream(filename);
		CertificateFactory f = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
		return certificate;
	}
	
}

