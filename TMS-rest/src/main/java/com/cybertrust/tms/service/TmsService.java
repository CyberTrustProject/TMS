package com.cybertrust.tms.service;

import java.util.List;

import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.DeviceHistoricalTrustData;
import com.cybertrust.tms.entity.DeviceHistoricalTrustDataId;
import com.cybertrust.tms.entity.PeerTms;
import com.cybertrust.tms.entity.TrustAssessment;
import com.cybertrust.tms.entity.TrustAssessmentId;
import com.cybertrust.tms.entity.TrustedUser;

public interface TmsService {
	
	//Trusted Users CRUD
	public TrustedUser getTrustedUser(int id);
	
	public TrustedUser getTrustedUserByTrustedUserId(String trustedUserId);
	
	public List<TrustedUser> getTrustedUsers();
	
	public void saveTrustedUser(TrustedUser trustedUser);
	
	public void deleteTrustedUser(String id);
	
	//Devices CRUD
	public Device getDevice(int id);
	
	public Device getDeviceByDeviceId(String deviceId);
	
	public List<Device> getDevices();
	
	public void saveDevice(Device device);
	
	public void createDevice(Device device);
	
	public Device saveDeviceWithUser(int userId, Device device);
	
	public void deleteDevice(int id);
	
	//PeerTms CRUD
	public PeerTms getPeerTms(int id);
	
	public PeerTms getPeerTmsByPeerTmsId(String peerTmsId);
	
	public List<PeerTms> getPeerTms(List<String> peerTMSids);
	
	public List<PeerTms> getPeerTmsAll();
	
	public void savePeerTms(PeerTms peerTms);
	
	public void updatePeerTms(PeerTms peerTms);
	
	public void deletePeerTms(String peerTmsId);
	
	//TrustAssessments CRUD
	public TrustAssessment getTrustAssessment(TrustAssessmentId id);
	
	public List<TrustAssessment> getTrustAssessments();
	
	public void saveTrustAssessment(TrustAssessment trustAssessment);
	
	public void deleteTrustAssessment(TrustAssessmentId id);
	
	//DeviceHistoricalTrustData CRUD
	public DeviceHistoricalTrustData getDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id);
	
	public List<DeviceHistoricalTrustData> getDeviceHistoricalTrustDataList();
	
	public void saveDeviceHistoricalTrustData(DeviceHistoricalTrustData deviceHistorical);
	
	public void deleteDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id);
	
	//Risk CRUD
	public List<Device> getRisksPrioritized(int numRisks);

}