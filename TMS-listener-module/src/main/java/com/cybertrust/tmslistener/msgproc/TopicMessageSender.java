package com.cybertrust.tmslistener.msgproc;

import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicMessageSender {

	private static class ConnectionInfo {
		Connection c;
		Session s;
		HashMap<String, MessageProducer > topicToProducer = new HashMap<String, MessageProducer>();
	};
	
	private static HashMap<String, ConnectionInfo> openConnections = new HashMap<String, ConnectionInfo>();

	public static void sendTo(String URI, String topic, String message) throws JMSException, Exception {
		ConnectionInfo conInfo = openConnections.get(URI);
		if (conInfo == null) {
			ConnectionFactory cf = new ActiveMQConnectionFactory(URI);
			conInfo = new ConnectionInfo();
			try {
				conInfo.c = cf.createConnection();
				conInfo.s =conInfo.c.createSession(false, Session.AUTO_ACKNOWLEDGE);
				conInfo.c.start();
				openConnections.put(URI, conInfo);
			} 
			catch (JMSException e) {
				throw new Exception(e.getMessage(), e.getCause());
			}
		}
		
		MessageProducer mp = conInfo.topicToProducer.get(topic);
		if (mp == null) {
			Topic topicObj = conInfo.s.createTopic(topic);
			mp = conInfo.s.createProducer(topicObj);
			conInfo.topicToProducer.put(topic, mp);
		}
		Message msg = conInfo.s.createTextMessage(message);
		mp.send(msg);
	}
	
	public static void clearAllConnections(String URI) throws Exception {
		ConnectionInfo conInfo = openConnections.get(URI);
		if (conInfo == null) {
			throw new Exception("No subscriptions to URI " + URI + " are registered.");
		}
		for (MessageProducer mp: conInfo.topicToProducer.values()) {
			mp.close();
		}
		conInfo.s.close();
		conInfo.c.close();
		openConnections.remove(URI);
	}
	
	public static void shutdownAll() throws Exception {
		for (String URI: openConnections.keySet())
			clearAllConnections(URI);
	}	
	
}
