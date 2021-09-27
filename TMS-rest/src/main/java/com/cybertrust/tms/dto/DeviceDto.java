package com.cybertrust.tms.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DeviceDto {
	
	private String deviceId;
	
	private String name;
	
	//@JsonIgnore
	private String description;
	
	@JsonIgnore
	private float trustLevel;
	
	@JsonIgnore
	private float status;
	
	@JsonIgnore
	private float behavior;
	
	@JsonIgnore
	private String explicitLevel;
	
	@JsonIgnore
	private String trustLevelBaseData;
	
	@JsonIgnore
	private LocalDateTime lastTrustTimestamp;
	
	private int userId;
	
	private String publicKey;
	
	/**
	 * @return the id
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param id the id to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the trustLevel
	 */
	public float getTrustLevel() {
		return trustLevel;
	}
	/**
	 * @param trustLevel the trustLevel to set
	 */
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
	/**
	 * @return the trustLevelBaseData
	 */
	public String getTrustLevelBaseData() {
		return trustLevelBaseData;
	}
	/**
	 * @param trustLevelBaseData the trustLevelBaseData to set
	 */
	public void setTrustLevelBaseData(String trustLevelBaseData) {
		this.trustLevelBaseData = trustLevelBaseData;
	}
	
	public LocalDateTime getLastTrustTimestamp() {
		return lastTrustTimestamp;
	}
	
	public void setLastTrustTimestamp(LocalDateTime lastTrustTimestamp) {
		this.lastTrustTimestamp = lastTrustTimestamp;
	}
	
	public String getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
