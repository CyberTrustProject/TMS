package com.cybertrust.tmslistener.entity;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;

@Entity
@Table(name="devicehistoricaltrustdata")
@Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
@IdClass(DeviceHistoricalTrustDataId.class)
public class DeviceHistoricalTrustData {
	
	@Id
	@Column(name="deviceId")
	private int deviceId;
	
	@Id
	@Column(name="tstamp")
	private LocalDateTime tstamp;
	
	@Column(name="trustLevel")
	private float trustLevel;
	
	@Column(name="statusIntegrity")
	private boolean statusIntegrity;
	
	@Column(name="statusVulns")
	private float statusVulns;
	
	@Column(name="status")
	private float status;
	
	@Column(name="compliance")
	private float compliance;
	
	@Column(name="nominality")
	private float nominality;
	
	@Column(name="malicious")
	private boolean malicious;
	
	@Column(name="behavior")
	private float behavior;
	
	@Column(name="compromiseRisk")
	private float compromiseRisk;
	
	@Column(name="explicitImpact")
	private String explicitImpact;
	
	@Column(name="singularRisk")
	private float singularRisk;
	
	@Column(name="neighborRisk")
	private float neighborRisk;
	
	@Column(name="associatedRisk")
	private float associatedRisk;
	
	@Column(name="explicitLevel")
	private String explicitLevel;
	
	@Column(name="trustLevelBaseData")
	private String trustLevelBaseData;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name="deviceId")
	@MapsId("deviceId")
	private Device device;
	
	public DeviceHistoricalTrustData() {
		
	}

	public DeviceHistoricalTrustData(int deviceId, LocalDateTime tstamp, float trustLevel, float status, float behavior,
			 String explicitLevel, String trustLevelBaseData) {
		this.deviceId = deviceId;
		this.tstamp = tstamp;
		this.trustLevel = trustLevel;
		this.status = status;
		this.behavior = behavior;
		this.explicitLevel = explicitLevel;
		this.trustLevelBaseData = trustLevelBaseData;
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

	public String getTrustLevelBaseData() {
		return trustLevelBaseData;
	}

	public void setTrustLevelBaseData(String trustLevelBaseData) {
		this.trustLevelBaseData = trustLevelBaseData;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public boolean isStatusIntegrity() {
		return statusIntegrity;
	}

	public void setStatusIntegrity(boolean statusIntegrity) {
		this.statusIntegrity = statusIntegrity;
	}

	public float getStatusVulns() {
		return statusVulns;
	}

	public void setStatusVulns(float statusVulns) {
		this.statusVulns = statusVulns;
	}

	public float getCompromiseRisk() {
		return compromiseRisk;
	}

	public void setCompromiseRisk(float compromiseRisk) {
		this.compromiseRisk = compromiseRisk;
	}

	public String getExplicitImpact() {
		return explicitImpact;
	}

	public void setExplicitImpact(String explicitImpact) {
		this.explicitImpact = explicitImpact;
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

	public float getAssociatedRisk() {
		return associatedRisk;
	}

	public void setAssociatedRisk(float associatedRisk) {
		this.associatedRisk = associatedRisk;
	}

	public float getCompliance() {
		return compliance;
	}

	public void setCompliance(float compliance) {
		this.compliance = compliance;
	}

	public float getNominality() {
		return nominality;
	}

	public void setNominality(float nominality) {
		this.nominality = nominality;
	}

	public boolean getMalicious() {
		return malicious;
	}

	public void setMalicious(boolean malicious) {
		this.malicious = malicious;
	}

}
