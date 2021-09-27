package com.cybertrust.tmslistener.msgproc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.cybertrust.busmessages.GenericMessage;
import com.cybertrust.cryptoutils.ConfigManagement;
import com.cybertrust.tmslistener.Config;

public class MessageSender {

	private static String cybertrustBusURI = null;
	
	public static void init(String connectionURI) {
		if (connectionURI == null)
			throw new IllegalStateException("TopicMessageSender: init: cannot initialize with null URL");	
		cybertrustBusURI = connectionURI;
	}
	
	public static void publish(int deviceId, String externalDevId, String deviceIP, float currentLevel, float previousLevel, String description, String type) throws Exception {
		if (cybertrustBusURI == null)
			throw new IllegalStateException("TopicMessageSender: sendTo: class is not initialized");
		
		String uri = cybertrustBusURI;
		GenericMessage m = new GenericMessage();
		
		m.setSource(ConfigManagement.getMyName());
		m.setMsgTopic(type);
		m.setTimestamp(new Date().getTime());
		m.addPayloadField("deviceId", externalDevId);
		
		if(currentLevel > previousLevel)
			m.addPayloadField("changeType", "elevation");
		else
			m.addPayloadField("changeType", "demotion");
		
		if(type.equals("trustLevelChange")) { 
			m.addPayloadField("currentTrustLevel", currentLevel);
			m.addPayloadField("previousTrustLevel", previousLevel);
		}
		else {
			m.addPayloadField("currentRiskLevel", currentLevel);
			m.addPayloadField("previousRiskLevel", previousLevel);
		}
		
		m.addPayloadField("description", description);
		m.addPayloadField("deviceIP", deviceIP);
		m.sign();
		String textMessage = m.toJSON();
		
		if(type.equals("trustLevelChange"))
			TopicMessageSender.sendTo(uri, Config.getTrustPublicationTopic(), textMessage);
		else
			TopicMessageSender.sendTo(uri, Config.getRiskPublicationTopic(), textMessage);
		System.out.println(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
		        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()) + " Sent message: " + textMessage);		
	}
}

