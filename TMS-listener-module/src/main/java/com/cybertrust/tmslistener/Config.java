package com.cybertrust.tmslistener;


import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Config {

	private static final String ConnectionUriPropName = "cybertrust.bus.connectionURI";
	private static final String RiskPublicationTopicPropName ="cybertrust.bus.riskPublicationTopic";
	private static final String TrustPublicationTopicPropName ="cybertrust.bus.trustPublicationTopic";
	private static final String TopicsToSubscribePropNamePrefix = "cybertrust.bus.subscriptionTopic";
	private static final String TrustRestorationPeriodPropName = "tms.trustRestorationPeriod";
	private static final String TrustRestorationFactorPropName = "tms.trustRestorationFactor";
	private static final String TrustRestorationPeriodDefaultValue = "3600";
	private static final String TrustRestorationFactorDefaultValue = "0.3";
	private static final String InvalidMessageBehaviorPropName = "com.cybertrust.tms.invalidSignatureBehavior";
	private static final String InvalidMessageBehaviorDefaultValue = "drop";
	public static final int InvalidMessageBehaviorDrop = 0;
	public static final int InvalidMessageBehaviorWarning = 1;
	
	private static String connectionUri = null;
	private static String riskPublicationTopic = null;
	private static String trustPublicationTopic = null;
	private static int trustRestorationPeriod = -1;
	private static float trustRestorationFactor = -1;
	private static Set<String> subscriptionTopics = new HashSet<String>();
	private static int invalidMessageBehavior = 0;

   /**
	 * @return the invalidMessageBehavior
	 */
	public static int getInvalidMessageBehavior() {
		return invalidMessageBehavior;
	}

private static boolean isInitialized = false;
     
	public static void initializeConfig(Properties props)  {
		connectionUri = props.getProperty(ConnectionUriPropName);
		if (connectionUri == null) {
			throw new IllegalStateException(ConnectionUriPropName + "is undefined in properties");
		}
		riskPublicationTopic = props.getProperty(RiskPublicationTopicPropName);
		if (riskPublicationTopic == null) {
			throw new IllegalStateException(RiskPublicationTopicPropName + "is undefined in properties");
		}
		trustPublicationTopic = props.getProperty(TrustPublicationTopicPropName);
		if (trustPublicationTopic == null) {
			throw new IllegalStateException(TrustPublicationTopicPropName + "is undefined in properties");
		}
		String propValue = props.getProperty(TrustRestorationPeriodPropName, TrustRestorationPeriodDefaultValue);
		try {
			trustRestorationPeriod = Integer.parseInt(propValue);
			if (trustRestorationPeriod <= 0)
				throw new IllegalStateException("The value of property " + TrustRestorationPeriodPropName + " (" + propValue + ") must be >= 0");
		}
		catch (Exception e) {
			throw new IllegalStateException("The value of property " + TrustRestorationPeriodPropName + " (" + propValue + ") is not a valid integer");
		}

		propValue = props.getProperty(TrustRestorationFactorPropName, TrustRestorationFactorDefaultValue);
		try {
			trustRestorationFactor = Float.parseFloat(propValue);
			if (trustRestorationFactor < 0)
				throw new IllegalStateException("The value of property " + TrustRestorationFactorPropName + " (" + propValue + ") cannot be < 0");
		}
		catch (Exception e) {
			throw new IllegalStateException("The value of property " + TrustRestorationFactorPropName + " (" + propValue + ") is not a valid float");
		}	

		propValue = props.getProperty(InvalidMessageBehaviorPropName, InvalidMessageBehaviorDefaultValue);
		try {
			if (propValue.equals("drop"))
				invalidMessageBehavior = InvalidMessageBehaviorDrop;
			else if (propValue.equals("warning"))
				invalidMessageBehavior = InvalidMessageBehaviorWarning;
			else
				throw new IllegalStateException("The value of property " + InvalidMessageBehaviorPropName + " (" + propValue + ") is invalid; acceptable values are drop and warning");
		}
		catch (Exception e) {
			throw new IllegalStateException("The value of property " + TrustRestorationFactorPropName + " (" + propValue + ") is not a valid float");
		}
		
		isInitialized = true;
		
		int subscriptionTopicCounter = 0;
		do {
			String subscriptionTopicPropName = TopicsToSubscribePropNamePrefix + String.format(".%d", subscriptionTopicCounter);
			String topic = props.getProperty(subscriptionTopicPropName);
			if (topic == null)
				break;
			subscriptionTopics.add(topic);
			subscriptionTopicCounter++;
		} while (true);
		if (subscriptionTopics.isEmpty()) {
			System.err.println("SEVERE: no topics to subscribe are defined; listener will not listen to anything");
		}
	}

	private static void testInitialized() {
		if (!isInitialized)
			throw new IllegalStateException("The configuration has not been initialized");
	}
	
	public static String getConnectionUri() {
		testInitialized();
		return connectionUri;
	}
	/**
	 * @return the myName
	 */
	public static String getRiskPublicationTopic() {
		testInitialized();
		return riskPublicationTopic;
	}

	public static String getTrustPublicationTopic() {
		testInitialized();
		return trustPublicationTopic;
	}	
	
	public static int getTrustRestorationPeriod() {
		testInitialized();
		return trustRestorationPeriod;
	}

	public static float getTrustRestorationFactor() {
		testInitialized();
		return trustRestorationFactor;
	}

	public static Collection<String> getSubscriptionTopics() {
		return subscriptionTopics;
	}
}
