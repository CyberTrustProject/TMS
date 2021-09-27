package com.cybertrust.tms.dto;

public class TrustedUserDto {

	public TrustedUserDto() {
		super();
	}

	private String trustedUserId;
	private String description;
	private float trustLevel;
	private String extraInfo;
	
	public String getTrustedUserId() {
		return trustedUserId;
	}
	
	public void setTrustedUserId(String trustedUserId) {
		this.trustedUserId = trustedUserId;
	}
	
	public float getTrustLevel() {
		return trustLevel;
	}
	
	public void setTrustLevel(float trustLevel) {
		this.trustLevel = trustLevel;
	}
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getExtraInfo() {
		return extraInfo;
	}
	
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

}
