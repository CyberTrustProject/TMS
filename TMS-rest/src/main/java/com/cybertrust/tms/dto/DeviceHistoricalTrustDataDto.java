package com.cybertrust.tms.dto;

import java.util.Date;

public class DeviceHistoricalTrustDataDto {
	
	private int deviceId;
	
	private Date tstamp;
	
	private float trustLevel;
	
	private float status;
	
	private float behavior;
	
	private String explicitLevel;
	
	//private String trustLevelBaseData;

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public Date getTstamp() {
		return tstamp;
	}

	public void setTstamp(Date tstamp) {
		this.tstamp = tstamp;
	}

	public float getTrustLevel() {
		return trustLevel;
	}

	public void setTrustLevel(float trustLevel) {
		this.trustLevel = trustLevel;
	}

	public float getStatus() {
		return status;
	}

	public void setStatus(float status) {
		this.status = status;
	}

	public float getBehavior() {
		return behavior;
	}

	public void setBehavior(float behavior) {
		this.behavior = behavior;
	}

	public String getExplicitLevel() {
		return explicitLevel;
	}

	public void setExplicitLevel(String explicitLevel) {
		this.explicitLevel = explicitLevel;
	}


}
