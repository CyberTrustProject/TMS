package com.cybertrust.tmslistener.entity;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;


@Entity
//@Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
@Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
@Table(name="device")
public class Device {
	
	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="deviceId", unique=true, length=36)
	private String deviceId;
	
	@Column(name="name", unique=true)
	private String name;
	
	@Column(name="description")
	private String description;
	
	@Column(name="ip", unique=true)
	private String ip;
	
	@Column(name="otherIpAddresses")
	private String otherIpAddresses;
	
	@Column(name="trustLevel")
	private float trustLevel;
	
	//used to calculate the integrity and vulnerability dimensions
	@Column(name="compromisedElements")
	private String compromisedElements;
	
	@Column(name="vulnerabilities")
	private String vulnerabilities;
	
	//used to calculate overall status trust
	@Column(name="statusIntegrity")
	private boolean statusIntegrity;
	
	@Column(name="statusVulns")
	private float statusVulns;
	
	//overall status trust
	@Column(name="status")
	private float status;
	
	//used to calculate overall behavior trust
	@Column(name="compliance")
	private float compliance;
	
	@Column(name="nominality")
	private float nominality;
	
	@Column(name="malicious")
	private boolean malicious;
	
	//overall behavior trust
	@Column(name="behavior")
	private float behavior;
	
	//used to calculate singular and neighbor risk
	@Column(name="compromiseRisk")
	private float compromiseRisk;
	
	@Column(name="explicitImpact")
	private String explicitImpact;
	
	@Column(name="directlyConnectedDevices")
	private String directlyConnectedDevices;
	
	//used to calculate overall risk trust
	@Column(name="singularRisk")
	private float singularRisk;
	
	@Column(name="neighborRisk")
	private float neighborRisk;
	
	//overall risk trust
	@Column(name="associatedRisk")
	private float associatedRisk;
	
	@Column(name="explicitLevel")
	private String explicitLevel;
	
	@Column(name="trustLevelBaseData")
	private String trustLevelBaseData;
	
	@Column(name="lastTrustTimestamp")
	private LocalDateTime lastTrustTimestamp;
	
	@Column(name="publicKey")
	private String publicKey;
	
	@ManyToOne(optional=true, cascade= {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JoinColumn(name="belongsTo")
	private TrustedUser user;

	public Device() {
		// defaults for new device
		trustLevel = 0.6f;
		status = 0;
		behavior = 1;
		compliance = 0.6f;
		nominality = 1;
		malicious = false;
	}

	public Device(int id, String name, String description, int belongsTo, float trustLevel, float status,
			float behavior, String explicitLevel, String trustLevelBaseData, LocalDateTime lastTrustTimestamp, String publicKey) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.trustLevel = trustLevel;
		this.status = status;
		this.behavior = behavior;
		this.explicitLevel = explicitLevel;
		this.trustLevelBaseData = trustLevelBaseData;
		this.lastTrustTimestamp = lastTrustTimestamp;
		this.publicKey = publicKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public float getTrustLevel() {
		return trustLevel;
	}

	public void setTrustLevel(float trustLevel) {
		this.trustLevel = trustLevel;
		//this.lastTrustTimestamp = LocalDateTime.now();
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
	
	/**
	 * @return the lastTrustTimestamp
	 */
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public float getAssociatedRisk() {
		return associatedRisk;
	}

	public void setAssociatedRisk(float associatedRisk) {
		this.associatedRisk = associatedRisk;
	}

	public float getCompromiseRisk() {
		return compromiseRisk;
	}

	public void setCompromiseRisk(float compromiseRisk) {
		this.compromiseRisk = compromiseRisk;
	}

	public String getCompromisedElements() {
		return compromisedElements;
	}

	public void setCompromisedElements(String compromisedElements) {
		this.compromisedElements = compromisedElements;
	}

	public String getVulnerabilities() {
		return vulnerabilities;
	}

	public void setVulnerabilities(String vulnerabilities) {
		this.vulnerabilities = vulnerabilities;
	}

	public String getDirectlyConnectedDevices() {
		return directlyConnectedDevices;
	}

	public void setDirectlyConnectedDevices(String directlyConnectedDevices) {
		this.directlyConnectedDevices = directlyConnectedDevices;
	}

	public String getOtherIpAddresses() {
		return otherIpAddresses;
	}

	public void setOtherIpAddresses(String otherIpAddresses) {
		this.otherIpAddresses = otherIpAddresses;
	}

	public boolean getStatusIntegrity() {
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public TrustedUser getUser() {
		return user;
	}

	public void setUser(TrustedUser user) {
		this.user = user;
	}

}