package com.cybertrust.tmslistener.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="peertms")
public class PeerTms {
	
	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="peerTmsId", unique=true, length=36)
	private String peerTmsId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="description")
	private String description;
	
	@Column(name="trustLevel")
	private float trustLevel;
	
	@Column(name="trustLevelBaseData")
	private String trustLevelBaseData;
	
	@Column(name="active", columnDefinition = "BOOLEAN", nullable=false)
	private boolean active;
	
    @Column(name = "publicKey")
    private String publicKey;
	
	@OneToOne(cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST
			, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JoinColumn(name="id")
	@MapsId
	private Device device;
	
	@OneToMany(mappedBy="peerTms", cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST
			, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	private List<TrustAssessment> trustAssessments;
	
	
	public PeerTms() {
		
	}

	public PeerTms(String description, float trustLevel, String trustLevelBaseData, boolean active) {
		this.description = description;
		this.trustLevel = trustLevel;
		this.trustLevelBaseData = trustLevelBaseData;
		this.active = active;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
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

	public float getTrustLevel() {
		return trustLevel;
	}

	public void setTrustLevel(float trustLevel) {
		this.trustLevel = trustLevel;
	}

	public String getTrustLevelBaseData() {
		return trustLevelBaseData;
	}

	public void setTrustLevelBaseData(String trustLevelBaseData) {
		this.trustLevelBaseData = trustLevelBaseData;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
	public List<TrustAssessment> getTrustAssessments() {
		return trustAssessments;
	}

	public void setTrustAssessments(List<TrustAssessment> trustAssessments) {
		this.trustAssessments = trustAssessments;
	}

	public void add(TrustAssessment trustAssessment) {
		
		if (trustAssessments == null) {
			trustAssessments = new ArrayList<>();
		}
		
		trustAssessments.add(trustAssessment);
		
		trustAssessment.getPeerTms();
		trustAssessment.setPeerTms(this);
		
	}

}
