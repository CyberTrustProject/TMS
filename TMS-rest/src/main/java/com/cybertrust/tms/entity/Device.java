package com.cybertrust.tms.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

import com.cybertrust.tms.rest.Views;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="device")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class Device {
	
	public Device(int id, String name, String description, float trustLevel, float status, float behavior,
			String explicitLevel, String trustLevelBaseData, TrustedUser user, PeerTms peerTms,
			List<TrustAssessment> trustAssessments) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.trustLevel = trustLevel;
		this.status = status;
		this.behavior = behavior;
		this.explicitLevel = explicitLevel;
		this.trustLevelBaseData = trustLevelBaseData;
		this.user = user;
		this.peerTms = peerTms;
		this.trustAssessments = trustAssessments;
	}

	@Id
	@Column(name="id")
	@JsonView(Views.Public.class)
	private int id;
	
	@Column(name="deviceId", unique=true, length=36)
	private String deviceId;
	
	@Column(name="name")
	@JsonView(Views.Public.class)
	private String name;
	
	@Column(name="description")
	@JsonView(Views.Public.class)
	private String description;
	
	@Column(name="ip", unique=true)
	private String ip;
	
	@Column(name="otherIpAddresses")
	private String otherIpAddresses;
	
	@Column(name="trustLevel")
	@JsonView(Views.Public.class)
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
	
	@Column(name="status")
	@JsonView(Views.Public.class)
	private float status;
	
	//used to calculate overall behavior trust
	@Column(name="compliance")
	private float compliance;
	
	@Column(name="nominality")
	private float nominality;
	
	@Column(name="malicious")
	private boolean malicious;
	
	@Column(name="behavior")
	@JsonView(Views.Public.class)
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
	@JsonView(Views.Public.class)
	private String explicitLevel;
	
	@Column(name="trustLevelBaseData")
	@JsonView(Views.Public.class)
	private String trustLevelBaseData;
	
	@Column(name="lastTrustTimestamp")
	private LocalDateTime lastTrustTimestamp;
	
    @Column(name = "publicKey")
    @JsonView(Views.Public.class)
    private String publicKey;
	
	@ManyToOne(optional=true, cascade= {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JoinColumn(name="belongsTo")
	@JsonView(Views.Internal.class)
	private TrustedUser user;
	
	@OneToOne(mappedBy="device", optional=true, cascade= {CascadeType.ALL}, fetch=FetchType.LAZY)
	@JsonView(Views.Internal.class)
	private PeerTms peerTms;
	
	@OneToMany(mappedBy="device", cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST
			, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JsonView(Views.Internal.class)
	private List<TrustAssessment> trustAssessments;
	
	@OneToMany(mappedBy="device", cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST
			, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JsonView(Views.Internal.class)
	private List<DeviceHistoricalTrustData> deviceHistoricalTrustData;
	
	public Device() {
		;
	}

	public Device(String description, float trustLevel, float status, float behavior, String explicitLevel,
			String trustLevelBaseData, LocalDateTime lastTrustTimestamp, String publicKey) {
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

	public TrustedUser getUser() {
		return user;
	}

	public void setUser(TrustedUser user) {
		this.user = user;
	}

	public PeerTms getPeerTms() {
		return peerTms;
	}

	public void setPeerTms(PeerTms peerTms) {
		this.peerTms = peerTms;
	}
	
	public List<TrustAssessment> getTrustAssessments() {
		return trustAssessments;
	}

	public void setTrustAssessments(List<TrustAssessment> trustAssessments) {
		this.trustAssessments = trustAssessments;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOtherIpAddresses() {
		return otherIpAddresses;
	}

	public void setOtherIpAddresses(String otherIpAddresses) {
		this.otherIpAddresses = otherIpAddresses;
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

	public String getDirectlyConnectedDevices() {
		return directlyConnectedDevices;
	}

	public void setDirectlyConnectedDevices(String directlyConnectedDevices) {
		this.directlyConnectedDevices = directlyConnectedDevices;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void add(TrustAssessment trustAssessment) {
		
		if (trustAssessments == null) {
			trustAssessments = new ArrayList<>();
		}
		
		trustAssessments.add(trustAssessment);
		
		trustAssessment.getDevice();
		trustAssessment.setDevice(this);
		
	}

	public List<DeviceHistoricalTrustData> getDeviceHistoricalTrustData() {
		return deviceHistoricalTrustData;
	}

	public void setDeviceHistoricalTrustData(List<DeviceHistoricalTrustData> deviceHistoricalTrustData) {
		this.deviceHistoricalTrustData = deviceHistoricalTrustData;
	}
	
	public DeviceHistoricalTrustData createHistoricalEntry() {
		
		DeviceHistoricalTrustData newEntry = new DeviceHistoricalTrustData();
    	
    	newEntry.setDeviceId(this.getId());
    	newEntry.setTrustLevel(this.getTrustLevel());
    	newEntry.setStatusIntegrity(this.getStatusIntegrity());
    	newEntry.setStatusVulns(this.getStatusVulns());
    	newEntry.setStatus(this.getStatus());
    	newEntry.setCompliance(this.getCompliance());
    	newEntry.setNominality(this.getNominality());
    	newEntry.setMalicious(this.getMalicious());
    	newEntry.setBehavior(this.getBehavior());
    	newEntry.setCompromiseRisk(this.getCompromiseRisk());
    	newEntry.setExplicitImpact(this.getExplicitImpact());
    	newEntry.setSingularRisk(this.getSingularRisk());
    	newEntry.setNeighborRisk(this.getNeighborRisk());
    	newEntry.setAssociatedRisk(this.getAssociatedRisk());
    	newEntry.setExplicitLevel(this.getExplicitLevel());
    	newEntry.setTrustLevelBaseData(this.getTrustLevelBaseData());
    	newEntry.setTstamp(this.getLastTrustTimestamp());
    	
    	return newEntry;
		
	}
	
	public void add(DeviceHistoricalTrustData deviceHistorical) {
		
		if (deviceHistoricalTrustData == null) {
			deviceHistoricalTrustData = new ArrayList<>();
		}
		
		deviceHistoricalTrustData.add(deviceHistorical);
		
		deviceHistorical.getDevice();
		deviceHistorical.setDevice(this);
		
	}

}
