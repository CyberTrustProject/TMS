package com.cybertrust.tms.dao;

import java.util.List;

import com.cybertrust.tms.entity.Device;

public interface DeviceDAO {
	
	public Device getDevice(int id);
	
	public Device getDeviceByDeviceId(String deviceId);
	
	public List<Device> getDevices();
	
	public void saveDevice(Device device);
	
	public void createDevice(Device device);	
	
	public void deleteDevice(int id);

}
