package com.cybertrust.tms.dto;

import java.util.Date;

public class TrustAssessmentDto {
	
	private int deviceId;
	
	private int tmsId;
	
	private Date tstamp;
	
	private float trustLevel;
	
	private float honesty;
	
	private float cooperativeness;
	
	private float communityInterest;

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

	public float getTrustLevel() {
		return trustLevel;
	}

	public void setTrustLevel(float trustLevel) {
		this.trustLevel = trustLevel;
	}

	public float getHonesty() {
		return honesty;
	}

	public void setHonesty(float honesty) {
		this.honesty = honesty;
	}

	public float getCooperativeness() {
		return cooperativeness;
	}

	public void setCooperativeness(float cooperativeness) {
		this.cooperativeness = cooperativeness;
	}

	public float getCommunityInterest() {
		return communityInterest;
	}

	public void setCommunityInterest(float communityInterest) {
		this.communityInterest = communityInterest;
	}

}
