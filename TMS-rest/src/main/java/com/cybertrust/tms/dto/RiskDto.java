package com.cybertrust.tms.dto;

public class RiskDto {
	
	private String deviceId;
	
	private float associatedRisk;
	
	private float singularRisk;
	
	private float neighborRisk;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public float getAssociatedRisk() {
		return associatedRisk;
	}

	public void setAssociatedRisk(float associatedRisk) {
		this.associatedRisk = associatedRisk;
	}

	public float getSingularRisk() {
		return singularRisk;
	}

	public void setSingularRisk(float singularRisk) {
		this.singularRisk = singularRisk;
	}

	public float getNeighborRisk() {
		return neighborRisk;
	}

	public void setNeighborRisk(float neighborRisk) {
		this.neighborRisk = neighborRisk;
	}

}
