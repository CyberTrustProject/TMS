package com.cybertrust.tms.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cybertrust.tms.dao.DeviceDAOImpl;
import com.cybertrust.tms.dao.DeviceHistoricalTrustDataDAO;
import com.cybertrust.tms.dao.PeerTmsDAO;
import com.cybertrust.tms.dao.RiskDAO;
import com.cybertrust.tms.dao.TrustAssessmentDAO;
import com.cybertrust.tms.dao.TrustedUserDAO;
import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.DeviceHistoricalTrustData;
import com.cybertrust.tms.entity.DeviceHistoricalTrustDataId;
import com.cybertrust.tms.entity.PeerTms;
import com.cybertrust.tms.entity.TrustAssessment;
import com.cybertrust.tms.entity.TrustAssessmentId;
import com.cybertrust.tms.entity.TrustedUser;

@Service
public class TmsServiceImpl implements TmsService {
	
	//Dependency Injections for DAOs
	@Autowired
	private TrustedUserDAO trustedUserDAO;
	
	@Autowired
	private DeviceDAOImpl deviceDAOImpl;
	
	@Autowired
	private PeerTmsDAO peerTmsDAO;
	
	@Autowired
	private TrustAssessmentDAO trustAssessmentDAO;
	
	@Autowired
	private DeviceHistoricalTrustDataDAO deviceHistoricalDAO;
	
	@Autowired
	private RiskDAO riskDAO;
	
	
	//Trusted Users CRUD
	
	@Override
	@Transactional
	public TrustedUser getTrustedUser(int id) {
		
		return trustedUserDAO.getTrustedUser(id);
	}
	
	@Override
	@Transactional
	public TrustedUser getTrustedUserByTrustedUserId(String trustedUserId) {
		
		return trustedUserDAO.getTrustedUserByTrustedUserId(trustedUserId);
	}

	@Override
	@Transactional
	public List<TrustedUser> getTrustedUsers() {
		
		return trustedUserDAO.getTrustedUsers();
	}

	@Override
	@Transactional
	public void saveTrustedUser(TrustedUser trustedUser) {
		
		trustedUserDAO.saveTrustedUser(trustedUser);
		
	}

	@Override
	@Transactional
	public void deleteTrustedUser(String id) {
		
		trustedUserDAO.deleteTrustedUser(id);
		
	}
	
	
	//Devices CRUD
	
	@Override
	@Transactional
	public Device getDevice(int id) {
		
		Device device = deviceDAOImpl.getDevice(id);
		if (device != null) {
			Hibernate.initialize(device.getUser());
			Hibernate.initialize(device.getDeviceHistoricalTrustData());
		}
		return device;
	}
	
	@Override
	@Transactional
	public Device getDeviceByDeviceId(String deviceId) {
		
		Device device = deviceDAOImpl.getDeviceByDeviceId(deviceId);
		if (device != null) {
			Hibernate.initialize(device.getUser());
			Hibernate.initialize(device.getDeviceHistoricalTrustData());
		}
		return device;
	}

	@Override
	@Transactional
	public List<Device> getDevices() {
		
		return deviceDAOImpl.getDevices();
		
	}

	@Override
	@Transactional
	public void saveDevice(Device device) {
		
		deviceDAOImpl.saveDevice(device);
		
	}

	@Override
	@Transactional
	public void createDevice(Device device) {
		deviceDAOImpl.createDevice(device);

	}
	
	@Override
	@Transactional
	public Device saveDeviceWithUser(int userId, Device device) {
		
		TrustedUser user = trustedUserDAO.getTrustedUser(userId);
		
		if(!user.getUser_devices().contains(device))
			user.add(device);
		deviceDAOImpl.saveDevice(device);
		//because of fetchType.LAZY
		Hibernate.initialize(device.getUser());
		return device;
		
	}

	@Override
	@Transactional
	public void deleteDevice(int id) {
		
		deviceDAOImpl.deleteDevice(id);
		
	}

	//PeerTms CRUD

	@Override
	@Transactional
	public PeerTms getPeerTms(int id) {
		
		return peerTmsDAO.getPeerTms(id);
		
	}
	
	@Override
	@Transactional
	public PeerTms getPeerTmsByPeerTmsId(String peerTmsId) {
		return peerTmsDAO.getPeerTmsByPeerTmsId(peerTmsId);
	}

	@Override
	@Transactional
	public List<PeerTms> getPeerTms(List<String> peerTMSids) {
		return peerTmsDAO.getPeerTmsSome(peerTMSids);
	}
	
	@Override
	@Transactional
	public List<PeerTms> getPeerTmsAll() {
		
		return peerTmsDAO.getPeerTmsAll();
		
	}

	@Override
	@Transactional
	public void savePeerTms(PeerTms peerTms) {
		
		Device device = deviceDAOImpl.getDevice(peerTms.getId());
		peerTms.setDevice(device);
		peerTmsDAO.savePeerTms(peerTms);
		device.setPeerTms(peerTms);
		
		
	}

	@Override
	@Transactional
	public void updatePeerTms(PeerTms peerTms) {
		
		//if(device != null)
			//peerTms.setDevice(device);
		
		peerTmsDAO.updatePeerTms(peerTms);
		
	}

	@Override
	@Transactional
	public void deletePeerTms(String peerTmsId) {
		
		peerTmsDAO.deletePeerTms(peerTmsId);
		
	}
	
	//TrustAssessment CRUD
	
	@Override
	@Transactional
	public TrustAssessment getTrustAssessment(TrustAssessmentId id) {
		
		return trustAssessmentDAO.getTrustAssessment(id);
		
	}

	@Override
	@Transactional
	public List<TrustAssessment> getTrustAssessments() {
		
		return trustAssessmentDAO.getTrustAssessments();
		
	}

	@Override
	@Transactional
	public void saveTrustAssessment(TrustAssessment trustAssessment) {
		
		PeerTms peerTms = peerTmsDAO.getPeerTms(trustAssessment.getTmsId());
		Device device = deviceDAOImpl.getDevice(trustAssessment.getDeviceId());
		
		peerTms.add(trustAssessment);
		device.add(trustAssessment);
		
		trustAssessmentDAO.saveTrustAssessment(trustAssessment);
		
	}

	@Override
	@Transactional
	public void deleteTrustAssessment(TrustAssessmentId id) {
		
		trustAssessmentDAO.deleteTrustAssessment(id);
		
	}
	
	//DeviceHistoricalTrustData CRUD

	@Override
	@Transactional
	public DeviceHistoricalTrustData getDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id) {
		return deviceHistoricalDAO.getDeviceHistoricalTrustData(id);
	}

	@Override
	@Transactional
	public List<DeviceHistoricalTrustData> getDeviceHistoricalTrustDataList() {
		return deviceHistoricalDAO.getDeviceHistoricalTrustDataList();
	}

	@Override
	@Transactional
	public void saveDeviceHistoricalTrustData(DeviceHistoricalTrustData deviceHistorical) {
		
		Device device = deviceDAOImpl.getDevice(deviceHistorical.getDeviceId());
		
		device.add(deviceHistorical);
		
		deviceHistoricalDAO.saveDeviceHistoricalTrustData(deviceHistorical);
		
	}

	@Override
	@Transactional
	public void deleteDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id) {
		deviceHistoricalDAO.deleteDeviceHistoricalTrustData(id);
		
	}

	//Risk CRUD
	@Override
	@Transactional
	public List<Device> getRisksPrioritized(int numRisks) {
		return riskDAO.getPrioritizedRisks(numRisks);
	}

}
