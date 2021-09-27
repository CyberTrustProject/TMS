package com.cybertrust.tmslistener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cybertrust.authentication.AuthClient;
import com.cybertrust.cryptoutils.ConfigManagement;
import com.cybertrust.evdbclient.EvdbClient;
import com.cybertrust.tmslistener.config.HibernateUtil;
import com.cybertrust.tmslistener.dao.TrustedUserDAO;
import com.cybertrust.tmslistener.model.TrustModel;
import com.cybertrust.tmslistener.model.TrustProperty;
import com.cybertrust.tmslistener.msgproc.MessageConsumer;
import com.cybertrust.tmslistener.msgproc.MessageSender;
import com.cybertrust.tmslistener.msgproc.TopicSubscriptionManager;

public class TmsListener {

	public static void main(String[] args) throws Exception {

		//changed for testbed to tms-listener
		String propertiesFile = System.getProperty("PROP_FILE", "application.properties");
		//String propertiesFile = System.getProperty("PROP_FILE", "tms-listener.properties");
		Properties props = loadProperties(propertiesFile);
		if (props == null) {
			throw new Exception("Properties file not found");
		}
		Config.initializeConfig(props);
		
		final String URI = Config.getConnectionUri();
		
		com.cybertrust.cryptoutils.ConfigManagement.initializeConfig(props);
		//com.cybertrust.authentication.AuthClient.initClient(props);
		com.cybertrust.tmslistener.model.TrustModel.init(props);
		com.cybertrust.evdbclient.EvdbClient.initClient(props);
		com.cybertrust.tmslistener.msgproc.MessageSender.init(URI);
		
		int trustRestorationPeriod = Config.getTrustRestorationPeriod();
		float trustRestorationFactor = Config.getTrustRestorationFactor();
		
		System.out.println("Starting TMS listener...");

		TrustRestorer tr = new TrustRestorer(trustRestorationPeriod, trustRestorationFactor);
		tr.start();
		
		MessageConsumer.setConcurrencyLevel(TrustModel.getMaxConcurrentThreads());
		
		// ensure that the special "unknown" user exists in the database
		try (Session session = HibernateUtil.getSession();) {
			Transaction transact = null;
			transact = session.beginTransaction();
			TrustedUserDAO.registerUser("TMS-unknown-user-982fa54", "Default TMS unknown user", session);
			transact.commit();
			session.flush();
		}
		catch (Exception e) {
			; // do nothing, probably due to already existing user
		}
			
			
		
		
		for (String topic: Config.getSubscriptionTopics()) {
			try {
				MessageConsumer mc = new MessageConsumer(topic);
				TopicSubscriptionManager.subscribeTo(URI, topic, mc);
			} catch (Exception e) {
				System.err.println("ERROR: could not subscribe to topic " + topic);
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(400000);
		} catch (InterruptedException e) {
			System.err.println("WARNING: sleep was interrupted");
			e.printStackTrace();
		}

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
