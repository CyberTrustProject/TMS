package com.cybertrust.tmslistener.dao;

import java.util.Date;

import org.hibernate.Session;

import com.cybertrust.tmslistener.config.HibernateUtil;
import com.cybertrust.tmslistener.entity.Device;
import com.cybertrust.tmslistener.entity.DeviceHistoricalTrustData;

public class DeviceHistoricalTrustDataDAO {

	
	public static void addDeviceHistoricalTrustData(Device device, Session session) {
		
		int i = 0;
		if(session==null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			i = 1;
		}
		
    	DeviceHistoricalTrustData newEntry = new DeviceHistoricalTrustData();
    	
    	newEntry.setDeviceId(device.getId());
    	newEntry.setTrustLevel(device.getTrustLevel());
    	newEntry.setStatusIntegrity(device.getStatusIntegrity());
    	newEntry.setStatusVulns(device.getStatusVulns());
    	newEntry.setStatus(device.getStatus());
    	newEntry.setCompliance(device.getCompliance());
    	newEntry.setNominality(device.getNominality());
    	newEntry.setMalicious(device.getMalicious());
    	newEntry.setBehavior(device.getBehavior());
    	newEntry.setCompromiseRisk(device.getCompromiseRisk());
    	newEntry.setExplicitImpact(device.getExplicitImpact());
    	newEntry.setSingularRisk(device.getSingularRisk());
    	newEntry.setNeighborRisk(device.getNeighborRisk());
    	newEntry.setAssociatedRisk(device.getAssociatedRisk());
    	newEntry.setExplicitLevel(device.getExplicitLevel());
    	newEntry.setTrustLevelBaseData(device.getTrustLevelBaseData());
    	newEntry.setTstamp(device.getLastTrustTimestamp());
		
		session.save(newEntry);
		
		if(i==1) {
			session.getTransaction().commit();
			session.close();
		}
			
		
	}

}
