package com.cybertrust.tmslistener.config;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;

import com.cybertrust.tmslistener.entity.Device;
import com.cybertrust.tmslistener.entity.DeviceHistoricalTrustData;
import com.cybertrust.tmslistener.entity.PeerTms;
import com.cybertrust.tmslistener.entity.TrustAssessment;
import com.cybertrust.tmslistener.entity.TrustedUser;

public class HibernateUtil {
	
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
        	Configuration theConfig = new Configuration();
        	String hibernateConfigFile = System.getProperty("HIBERNATE_CFG", null);
        	
        	if (hibernateConfigFile != null) {
        		// Create the SessionFactory from designated file
	            theConfig.configure(new File(hibernateConfigFile));
        	}
        	else {
        		// Create the SessionFactory from built-in config
	            theConfig.configure("hibernate.cfg.xml");
        	}
        	return theConfig
        			.addAnnotatedClass(TrustedUser.class)
            		.addAnnotatedClass(Device.class)
            		.addAnnotatedClass(DeviceHistoricalTrustData.class)
            		.addAnnotatedClass(PeerTms.class)
            		.addAnnotatedClass(TrustAssessment.class)
            		.buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
    	return HibernateUtil.getSessionFactory().openSession();
    	// or HibernateUtil.getSessionFactory().getCurrentSession(); - this leads to 'transaction already active' errors
    }
    
    public static void releaseSession(Session s) {
    	s.close();
    }
    
    
}
