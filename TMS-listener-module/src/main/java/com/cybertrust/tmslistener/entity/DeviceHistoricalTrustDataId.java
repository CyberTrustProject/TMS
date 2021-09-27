package com.cybertrust.tmslistener.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DeviceHistoricalTrustDataId implements Serializable{
	
	private int deviceId;
	
	//private LocalDateTime tstamp = LocalDateTime.now();
	private LocalDateTime tstamp;
	
	public DeviceHistoricalTrustDataId() {
		
	}

	public DeviceHistoricalTrustDataId(int deviceId, LocalDateTime tstamp) {
		this.deviceId = deviceId;
		this.tstamp = tstamp;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public LocalDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(LocalDateTime tstamp) {
		this.tstamp = tstamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviceId;
		result = prime * result + ((tstamp == null) ? 0 : tstamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceHistoricalTrustDataId other = (DeviceHistoricalTrustDataId) obj;
		if (deviceId != other.deviceId)
			return false;
		if (tstamp == null) {
			if (other.tstamp != null)
				return false;
		} else if (!tstamp.equals(other.tstamp))
			return false;
		return true;
	}

}
