package com.cybertrust.cryptoutils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManagement {
	
   private static String signatureAlgorithm;
   private static String myName;
   private static String myCertificate;
   private static RSAPublicKey myPublicKey;
   private static RSAPrivateKey myPrivateKey;
   private static HashMap<String, RSAPublicKey> peerPubKeys = new HashMap<String, RSAPublicKey>();
   private static boolean isInitialized = false;
   
   // Config file in which any changes will be saved; passed externally
   private static String configFilename = null;
   
public static void initializeConfig(Properties props)  {
	signatureAlgorithm = props.getProperty("cybertrust.crypto.signatureAlgorithm");
	myName = props.getProperty("cybertrust.crypto.myName");
    try {
    	try {
    		myCertificate = new String(Files.readAllBytes(Paths.get(props.getProperty("cybertrust.crypto.myCertificate"))));
    		myPublicKey = Loader.getPublicKeyFromCertificateFile(props.getProperty("cybertrust.crypto.myCertificate"));
    	}
    	catch (Exception e) {
    		throw new IllegalStateException("cybertrust.crypto.myCertificate is empty, the file is not found or it contains invalid data");
    	}
    	try {
    		String privKeyFilename=props.getProperty("cybertrust.crypto.myPrivateKey");
    		myPrivateKey = Loader.getPrivateKeyFromFile(privKeyFilename);
    	}
    	catch (Exception e) {
    		System.err.println("Error loading file " + props.getProperty("cybertrust.crypto.myPrivateKey"));
    		System.err.println(e.toString());
    		throw new IllegalStateException("cybertrust.crypto.myPrivateKey is empty, the file is not found or it contains invalid data");
    	}
    	peerPubKeys.put(myName, myPublicKey);
    	int peerCounter = 0;
    	do {
    		String peerNameProp = String.format("cybertrust.crypto.peerModules.%d.name", peerCounter);
    		String peerName = props.getProperty(peerNameProp);
    		if (peerName == null)
    			break;
    		String peerNameCertFileProp = String.format("cybertrust.crypto.peerModules.%d.certificate", peerCounter);
    		String peerNameCertFile = props.getProperty(peerNameCertFileProp);
    		if (peerNameCertFile == null) // Do not halt the program, produce though an error
    			Logger.getLogger("ConfigManagement").log(Level.SEVERE, 
    					String.format("Property %s not found while property %s is defined", peerNameCertFile, peerNameProp));
    			// instantiate public key from file
    			try {
    				RSAPublicKey peerRsaPubKey = Loader.getPublicKeyFromCertificateFile(peerNameCertFile); 
    				peerPubKeys.put(peerName, peerRsaPubKey);
    			}
    			catch (Exception e) {
        			Logger.getLogger("ConfigManagement").log(Level.SEVERE, 
        					String.format("File %s specified in property %s not found or does not contains a valid RSA key", peerNameCertFile, peerNameCertFileProp));    			}
    			peerCounter++;
    	} while (true);
    }
    catch (Exception e) {
    	throw(e);
    }
	if ((myPublicKey == null) || (signatureAlgorithm == null) || (myName == null))
		throw new IllegalStateException("one of the properties cybertrust.crypto.signatureAlgorithm, cybertrust.crypto.myName, cybertrust.crypto.myPublicKey, cybertrust.crypto.myPrivateKey is not defined");	
	isInitialized = true;
}

/*
 * Initialize the client, providing the config file name; this is useful so that the client can subsequently update the config file contents,
 * as new data are provided
 */

public static void initializeConfig(Properties props, String configFilename) {
	initializeConfig(props);
	ConfigManagement.configFilename = configFilename;
}

private static void testInitialized() {
	if (!isInitialized)
		throw new IllegalStateException("The configuration has not been initialized");
}

public static String getSignatureAlgorithm() {
	testInitialized();
	return signatureAlgorithm;
}
/**
 * @return the myName
 */
public static String getMyName() {
	testInitialized();
	return myName;
}

public static String getMyCertificate() {
	testInitialized();
	return myCertificate;
}

/**
 * @return the myPublicKey
 */
public static RSAPublicKey getMyPublicKey() {
	testInitialized();
	return myPublicKey;
}
/**
 * @return the myPrivateKey
 */
public static RSAPrivateKey getMyPrivateKey() {
	testInitialized();
	return myPrivateKey;
}
   
public static RSAPublicKey getPublicKey(String peerName) throws NoSuchElementException {
	testInitialized();
	RSAPublicKey result = peerPubKeys.get(peerName);
	if (result == null)
		throw new NoSuchElementException("No known key for module " + peerName);
	else
		return result;
}
}
