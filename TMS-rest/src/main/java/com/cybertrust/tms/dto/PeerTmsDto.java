package com.cybertrust.tms.dto;

public class PeerTmsDto {
	
	private String peerTmsId;
	
	private String deviceId;
	
	private String name;
	
	private String description;
	
	private String publicKey;

	public String getPeerTmsId() {
		return peerTmsId;
	}

	public void setPeerTmsId(String peerTmsId) {
		this.peerTmsId = peerTmsId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
}
