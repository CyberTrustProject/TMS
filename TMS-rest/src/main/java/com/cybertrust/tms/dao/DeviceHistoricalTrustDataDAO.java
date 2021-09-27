package com.cybertrust.tms.dao;

import java.util.List;

import com.cybertrust.tms.entity.DeviceHistoricalTrustData;
import com.cybertrust.tms.entity.DeviceHistoricalTrustDataId;

public interface DeviceHistoricalTrustDataDAO {
	
	public DeviceHistoricalTrustData getDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id);
	
	public List<DeviceHistoricalTrustData> getDeviceHistoricalTrustDataList();
	
	public void saveDeviceHistoricalTrustData(DeviceHistoricalTrustData trustAssessment);
	
	public void deleteDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id);

}
