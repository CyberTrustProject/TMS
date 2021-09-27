package com.cybertrust.tms.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonFormat;


public class TrustScoreElementDto {
	
	private String deviceId;
	
	private String status;
	
	private String computationType;
	
	private ArrayList<TrustDimensionScore> trustDimensionScores = new ArrayList<TrustDimensionScore>();
	
	private ArrayList<TrustDimensionScore> computedTrustDimensionScores = new ArrayList<TrustDimensionScore>();
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime when;
	
	private String trustComputationInfo;

	public TrustScoreElementDto(String deviceId, String status, String computationType,
			ArrayList<TrustDimensionScore> trustDimensionScores,
			ArrayList<TrustDimensionScore> computedTrustDimensionScores, LocalDateTime when, String trustComputationInfo) {
		this.deviceId = deviceId;
		this.status = status;
		this.computationType = computationType;
		this.trustDimensionScores = trustDimensionScores;
		this.computedTrustDimensionScores = computedTrustDimensionScores;
		this.when = when;
		this.trustComputationInfo = trustComputationInfo;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComputationType() {
		return computationType;
	}

	public void setComputationType(String computationType) {
		this.computationType = computationType;
	}

	public ArrayList<TrustDimensionScore> getTrustDimensionScores() {
		return trustDimensionScores;
	}

	public void setTrustDimensionScores(ArrayList<TrustDimensionScore> trustDimensionScores) {
		this.trustDimensionScores = trustDimensionScores;
	}

	public ArrayList<TrustDimensionScore> getComputedTrustDimensionScores() {
		return computedTrustDimensionScores;
	}

	public void setComputedTrustDimensionScores(ArrayList<TrustDimensionScore> computedTrustDimensionScores) {
		this.computedTrustDimensionScores = computedTrustDimensionScores;
	}
	
	public LocalDateTime getWhen() {
		return when;
	}

	public void setWhen(LocalDateTime when) {
		this.when = when;
	}

	public String getTrustComputationInfo() {
		return trustComputationInfo;
	}

	public void setTrustComputationInfo(String trustComputationInfo) {
		this.trustComputationInfo = trustComputationInfo;
	}

}
