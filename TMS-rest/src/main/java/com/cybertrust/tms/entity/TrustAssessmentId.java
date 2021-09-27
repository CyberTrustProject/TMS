package com.cybertrust.tms.entity;

import java.io.Serializable;
import java.util.Date;


public class TrustAssessmentId implements Serializable {
	
	int deviceId;
	
	int tmsId;
	
	private Date tstamp = new Date();
	
	
	public TrustAssessmentId() {
		
	}
	
	public TrustAssessmentId(int deviceId, int tmsId, Date tstamp) {
		this.deviceId = deviceId;
		this.tmsId = tmsId;
		this.tstamp = tstamp;
	}
	

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int getTmsId() {
		return tmsId;
	}

	public void setTmsId(int tmsId) {
		this.tmsId = tmsId;
	}
	
	public Date getTstamp() {
		return tstamp;
	}
	
	public void setTstamp(Date tstamp) {
		this.tstamp = tstamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviceId;
		result = prime * result + tmsId;
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
		TrustAssessmentId other = (TrustAssessmentId) obj;
		if (deviceId != other.deviceId)
			return false;
		if (tmsId != other.tmsId)
			return false;
		if (tstamp == null) {
			if (other.tstamp != null)
				return false;
		} else if (!tstamp.equals(other.tstamp))
			return false;
		return true;
	}
	
}
