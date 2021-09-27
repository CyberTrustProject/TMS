package com.cybertrust.tmslistener.dao;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.CacheMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.cybertrust.tmslistener.entity.Device;
import com.cybertrust.tmslistener.entity.TrustedUser;
import com.cybertrust.tmslistener.model.TrustModel;
import com.cybertrust.tmslistener.msgproc.MessageSender;

public class DeviceDAO {

	public static Device getDevice(int id, Session session) {

			Device device = session.get(Device.class, id);
			return device;
		
	}

	public static Device getDeviceBy(String deviceId, String name, String ip, Session session) {

		if((deviceId == null) && (name == null) && (ip == null))
			return null;

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Device> criteria = builder.createQuery(Device.class);
		Root<Device> from = criteria.from(Device.class);
		criteria.select(from);

		if(deviceId != null)
			criteria.where(builder.equal(from.get("deviceId"), deviceId));

		else if(name != null)
			criteria.where(builder.equal(from.get("name"), name));

		else if(ip != null)
			criteria.where(builder.equal(from.get("ip"), ip));

		TypedQuery<Device> typed = session.createQuery(criteria);
		session.setCacheMode(CacheMode.IGNORE);

		try {
			Device device = typed.getSingleResult();
			
			//TODO
			//We should see if this is needed in every method call, or if we should make it conditional
			//Hibernate.initialize(device.getTrustAssessments());
			
			return device;
		} catch (NoResultException e) {
			return null;
		}
	}

	
	public static Device getDeviceByAny(String name, String ip, Session session) {

		if((name == null) && (ip == null))
			return null;

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Device> criteria = builder.createQuery(Device.class);
		Root<Device> from = criteria.from(Device.class);
		criteria.select(from);
		
		List<Device> devices = new ArrayList<Device>();

		Predicate pNameToId = null, pNameToName = null, pNameDisjunction = null, pIp = null;
		
		if(name != null) {
			pNameToId = builder.equal(from.get("deviceId"), name);
			pNameToName = builder.equal(from.get("name"), name);
			pNameDisjunction = builder.or(pNameToId, pNameToName);
			pNameDisjunction = pNameToId; // comment this out to allow matching of names too
		}
		if((name == null) && (ip != null)) { // only try to match IP when name is not provided. This may be the case with network alerts
			pIp = builder.equal(from.get("ip"), ip);
		}

		if (pNameDisjunction == null)
			criteria.where(pIp);
		else if (pIp == null)
			criteria.where(pNameDisjunction);
		else
			criteria.where(builder.or(pNameDisjunction, pIp));
		
		TypedQuery<Device> typed = session.createQuery(criteria);
		session.setCacheMode(CacheMode.IGNORE);

		try {
			Device device = typed.getSingleResult();
			return device;
		} catch (NoResultException e) {
			return null;
		} catch (NonUniqueResultException k) {
			System.err.println("Found two different devices for deviceId = "+name+" and deviceIp = "+ip+"\n"
					+"Results:\n"+typed.getResultList().get(0).getDeviceId() +" "+typed.getResultList().get(0).getIp()+"\n"
					+typed.getResultList().get(1).getDeviceId() +" "+typed.getResultList().get(1).getIp()+"\n"
					+"Returning deviceId match. . .");
			return (typed.getResultList().get(0).getDeviceId().equals(name)) ? typed.getResultList().get(0) : typed.getResultList().get(1);
		}
	}
	
	
	public static float getDeviceTrust(int id, Session session) {

		float currentTrust = session.get(Device.class, id).getTrustLevel();
		return currentTrust;

	}

	public static void updateDevice(Device dev, Session session) {
		session.update(dev);
	}
	
	//Resolves the ConstraintViolation occurring when updating a device's IP with one that is already assigned to another device
	//The old device's IP is considered outdated and set to null
	public static void updateDeviceIp(Device dev, Device oldDev, String ip, Session session) {
		oldDev.setIp(null);
		session.flush();
		dev.setIp(ip);
		session.flush();
	}

	//TODO
	//may be redundant and updateDevice() could be enough
	//MessageConsumer could be changed to use only updateDevice() and reduce transactions overhead
	public static void updateDeviceTrust(Device device, float status, float behavior, float associatedRisk, String description, Session session) {

		float previousRiskLevel = -1;
		float previousTrustLevel = -1;
		float trustLevel;

		device.setBehavior(behavior);
		device.setStatus(status);

		/* Change of notification conditions to allow for more aggressive/frequent message sending. */
		/* if( (associatedRisk < TrustModel.getLowerTrustThreshold() && device.getAssociatedRisk() >= TrustModel.getLowerTrustThreshold()) ||
				(associatedRisk > TrustModel.getUpperTrustThreshold() && device.getAssociatedRisk() <= TrustModel.getUpperTrustThreshold()) ||
				( (associatedRisk >= 0.33f && associatedRisk <= 0.66f) && (device.getAssociatedRisk() < 0.33f || device.getAssociatedRisk() > 0.66f) ) ) */
		if (Math.abs(associatedRisk - device.getAssociatedRisk()) > 0.01) // Notification threshold: an 1% change
			previousRiskLevel = device.getAssociatedRisk();

		device.setAssociatedRisk(associatedRisk);

		trustLevel = TrustModel.localTrust(status, behavior, associatedRisk);

		/* Change of notification conditions to allow for more aggressive/frequent message sending. */
		/*		if( (trustLevel < TrustModel.getLowerTrustThreshold() && device.getTrustLevel() >= TrustModel.getLowerTrustThreshold()) ||
				(trustLevel > TrustModel.getUpperTrustThreshold() && device.getTrustLevel() <= TrustModel.getUpperTrustThreshold()) ||
				( (trustLevel >= 0.33f && trustLevel <= 0.66f) && (device.getTrustLevel() < 0.33f || device.getTrustLevel() > 0.66f) ) ) */
		if (Math.abs(trustLevel - device.getTrustLevel()) > 0.01) // Notification threshold: an 1% change		
			previousTrustLevel = device.getTrustLevel();

		device.setTrustLevel(trustLevel);
		//device.setLastTrustTimestamp(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
		device.setLastTrustTimestamp(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
		        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()));

		session.saveOrUpdate(device);
		session.flush();
		DeviceHistoricalTrustDataDAO.addDeviceHistoricalTrustData(device, session);

		if(true || (previousRiskLevel != -1))
			try {
				MessageSender.publish(device.getId(), device.getDeviceId(), device.getIp(), associatedRisk, previousRiskLevel, description, "riskLevelChange");
			} catch (Exception e) {
				e.printStackTrace();
			}

		if(true || (previousTrustLevel != -1))
			try {
				MessageSender.publish(device.getId(), device.getDeviceId(), device.getIp(), trustLevel, previousTrustLevel, description, "trustLevelChange");
			} catch (Exception e) {
				e.printStackTrace();
			}

	}
	
	public static Device registerDevice(String deviceId, TrustedUser user, String description, Session session) {

		Device device = new Device();
		device.setDeviceId(deviceId);
		device.setDescription(description);
		//device.setLastTrustTimestamp(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
		device.setLastTrustTimestamp(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
		        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()));

		if(user != null) {
			Hibernate.initialize(user.getUser_devices());
			user.add(device);
		}
		session.saveOrUpdate(device);

		return device;
	}

	public static List<Device> getRestorableDevices(LocalDateTime beforeTimestamp, Session session) {
		String hql = "FROM Device d WHERE lastTrustTimestamp < :beforeTimestamp";
		System.out.println(beforeTimestamp.toString());
		Query<Device> query = (Query<Device>)(session.createQuery(hql));
		query.setParameter("beforeTimestamp", beforeTimestamp);
		List<Device> results = query.list();
		return results;
	}

}
