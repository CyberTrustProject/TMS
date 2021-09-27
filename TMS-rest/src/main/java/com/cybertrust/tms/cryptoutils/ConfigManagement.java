package com.cybertrust.tms.cryptoutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Value;

public class ConfigManagement {
	
	@Value("${cybertrust.crypto.configFileDir}")
	private String configFileDir;
	@Value("${cybertrust.crypto.configFileName}")
	private String configFileName;
	@Value("${cybertrust.crypto.signatureAlgorithm}")
	private String signatureAlgorithm;
	@Value("${cybertrust.crypto.myName}")
	private String myName;
	@Value("${cybertrust.crypto.myCertificate}")
	private String myCertificateFile;
	@Value("${cybertrust.crypto.myPrivateKey}")
	private String myPrivateKey;
	private X509Certificate myCertificate;
	private RSAPublicKey myRSAPublicKey;
	private RSAPrivateKey myRSAPrivateKey;
	private HashMap<String, RSAPublicKey> peerPubKeys = new HashMap<String, RSAPublicKey>();
	private boolean isInitialized = false;
	int peerCounter;
	/**
	 * @return the signatureAlgorithm
	 */
	
	public ConfigManagement() {
		
	}
	
	@PostConstruct
	public void initializeConfig() throws Exception  {
		
		try {
			try {
				myCertificate = Loader.getCertFromCertFile(configFileDir + myCertificateFile);
				myRSAPublicKey = (RSAPublicKey) myCertificate.getPublicKey();
				//myRSAPublicKey = Loader.getPublicKeyFromCertificateFile(configFileDir + myCertificateFile);
			}
			catch (Exception e) {
				throw new IllegalStateException("cybertrust.crypto.myCertificate is empty, the file is not found or it contains invalid data");
			}
			try {
				myRSAPrivateKey = Loader.getPrivateKeyFromFile(configFileDir + myPrivateKey);
			}
			catch (Exception e) {
				throw new IllegalStateException("cybertrust.crypto.myPrivateKey is empty, the file is not found or it contains invalid data");
			}
			peerPubKeys.put(myName, myRSAPublicKey);
			
			Properties props = loadProperties(configFileDir + configFileName);
			if (props == null) {
				throw new Exception("Properties file not found");
			}
			
			peerCounter = 0;
			do {
				String peerNameProp = String.format("cybertrust.crypto.peerModules.%d.name", peerCounter);
				
				String peerName = props.getProperty(peerNameProp);
				//System.out.println("####TEST####\n" + peerNameProp + "\n" + peerName +"\n####TEST####");
				if (peerName == null)
					break;
				String peerNameCertFileProp = String.format("cybertrust.crypto.peerModules.%d.certificate", peerCounter);
				String peerNameCertFile = props.getProperty(peerNameCertFileProp);
				//System.out.println("####TEST####\n" + peerNameCertFileProp + "\n" + peerNameCertFile +"\n####TEST####");
				
				if (peerNameCertFile == null) // Do not halt the program, produce though an error
					Logger.getLogger("ConfigManagement").log(Level.SEVERE, 
							String.format("Property %s not found while property %s is defined", peerNameCertFile, peerNameProp));
				// instantiate public key from file
				try {
					RSAPublicKey peerRsaPubKey = Loader.getPublicKeyFromCertificateFile(configFileDir + peerNameCertFile); 
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
		if ((myRSAPublicKey == null) || (signatureAlgorithm == null) || (myName == null))
			throw new IllegalStateException("one of the properties cybertrust.crypto.signatureAlgorithm, cybertrust.crypto.myName, cybertrust.crypto.myPublicKey, cybertrust.crypto.myPrivateKey is not defined");
		isInitialized = true;
		
	}

	private void testInitialized() {
		if (!isInitialized)
			throw new IllegalStateException("The configuration has not been initialized");
	}

	public String getSignatureAlgorithm() {
		testInitialized();
		return signatureAlgorithm;
	}
	/**
	 * @return the myName
	 */
	public String getMyName() {
		testInitialized();
		return myName;
	}
	/**
	 * @return the myPublicKey
	 */
	public RSAPublicKey getMyRSAPublicKey() {
		testInitialized();
		return myRSAPublicKey;
	}
	/**
	 * @return the myPrivateKey
	 */
	public RSAPrivateKey getMyRSAPrivateKey() {
		testInitialized();
		return myRSAPrivateKey;
	}

	public RSAPublicKey getPublicKey(String peerName) throws NoSuchElementException {
		testInitialized();
		RSAPublicKey result = peerPubKeys.get(peerName);
		if (result == null)
			throw new NoSuchElementException("No known key for module " + peerName);
		else
			return result;
	}

	public void addTrustedEntity(String name, String certificate) throws IOException, GeneralSecurityException {

		testInitialized();

		try {
			if(name!=null && certificate!=null) {
				
				PemObject pem = Loader.getPemFromString(certificate);
				PemWriter pw = null;
				try {
					pw = new PemWriter(
						new FileWriter(new File(configFileDir, name.replace("cybertrust.eu", "cert.pem"))));
					pw.writeObject(pem);
					pw.flush();
					pw.close();
					pw = null;
				}
				catch (Exception e) {
					throw(e);
				}
				finally {
					if (pw != null)
						pw.close();
				}
					
				peerPubKeys.put(name, (RSAPublicKey) Loader.getCertificateFromString(certificate).getPublicKey());
				
				FileWriter fw = null;
				try {
					fw = new FileWriter(new File(configFileDir + configFileName), true);
					fw.write("\n\ncybertrust.crypto.peerModules." + peerCounter + ".name=" + name + "\n"
							+ "cybertrust.crypto.peerModules."+ peerCounter +".certificate=" + name.replace("cybertrust.eu", "cert.pem"));
					fw.flush();
					fw.close();
					fw = null;
					peerCounter++;
				}
				catch (Exception e) {
					throw(e);
				}
				finally {
					if (fw != null)
						fw.close();
				}
			}
			else
				throw new NullPointerException("Input was null");

		}
		catch(IOException e) {
			throw new IOException("String does not contain valid RSA Public Key");
		}
	}
	
	public void updateTrustedEntity (String name, String certificate) throws IOException {
		
		PemObject pem = null;
		PemWriter pw = null;

		try {
			Loader.getPemFromString(certificate);
			pw = new PemWriter(
					new FileWriter(new File(configFileDir, name.replace("cybertrust.eu", "cert.pem")), false));
			pw.writeObject(pem);
			pw.flush();
			pw.close();
			pw = null;
		}
		catch (Exception e) {
			throw(e);
		}
		finally {
			if (pw != null)
				pw.close();
		}
		
	}
	
	public String getStringCertificate() throws CertificateEncodingException, IOException {
		String cert = Loader.getKey(configFileDir + myCertificateFile);
		return cert;
	}
	
	private static Properties loadProperties(String fileName) throws IOException {
		FileInputStream fis = null;
		Properties prop = null;
	    try {
	        fis = new FileInputStream(fileName);
	        prop = new Properties();
	        prop.load(fis);
	    } catch(FileNotFoundException fnfe) {
	        fnfe.printStackTrace();
	    } catch(IOException ioe) {
	        ioe.printStackTrace();
	    } finally {
	    	if (fis != null)
	    		fis.close();
	    }
	    return prop;
	}

}
