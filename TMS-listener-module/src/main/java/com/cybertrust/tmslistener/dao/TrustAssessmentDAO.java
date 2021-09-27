package com.cybertrust.tmslistener.dao;

import java.time.LocalDateTime;

import org.hibernate.Session;

import com.cybertrust.tmslistener.entity.TrustAssessment;
import com.cybertrust.tmslistener.entity.TrustAssessmentId;

public class TrustAssessmentDAO {
	
	public static TrustAssessment getTrustAssessment(int deviceId, int tmsId, LocalDateTime tstamp, Session session) {
		
		TrustAssessmentId id = new TrustAssessmentId(deviceId, tmsId, tstamp);
		
		return session.get(TrustAssessment.class, id);
		
	}

}
