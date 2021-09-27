package com.cybertrust.tms.entity;

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

import com.cybertrust.tms.rest.Views;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="peertms")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
//@JsonFilter("peertmsFilter")
public class PeerTms {
	
	@Id
	@Column(name="id")
	@JsonView(Views.Internal.class)
	private int id;
	
	@Column(name="peerTmsId", unique=true, length=36)
	@JsonView(Views.Public.class)
	private String peerTmsId;
	
	@Column(name="name")
	@JsonView(Views.Public.class)
	private String name;
	
	@Column(name="description")
	@JsonView(Views.Public.class)
	private String description;
	
	@Column(name="trustLevel")
	@JsonView(Views.Public.class)
	private float trustLevel;
	
	@Column(name="trustLevelBaseData")
	@JsonView(Views.Internal.class)
	private String trustLevelBaseData;
	
	@Column(name="active", columnDefinition = "BOOLEAN", nullable=false)
	@JsonView(Views.Internal.class)
	private boolean active;
	
    @Column(name = "publicKey")
    @JsonView(Views.Public.class)
    private String publicKey;
	
	@OneToOne(cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST
			, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JoinColumn(name="id")
	@MapsId
	@JsonView(Views.Internal.class)
	private Device device;
	
	@OneToMany(mappedBy="peerTms", cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST
			, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JsonView(Views.Internal.class)
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
	
	public void add(TrustAssessment trustAssessment) {
		
		if (trustAssessments == null) {
			trustAssessments = new ArrayList<>();
		}
		
		trustAssessments.add(trustAssessment);
		
		trustAssessment.getPeerTms();
		trustAssessment.setPeerTms(this);
		
	}

}
