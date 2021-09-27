package com.cybertrust.tmslistener.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name="trustassessments")
@IdClass(TrustAssessmentId.class)
public class TrustAssessment {
	@Id
	@Column(name="deviceId")
	private int deviceId;
	
	@Id
	@Column(name="tmsId")
	private int tmsId;
	
	@Id
	@Column(name="tstamp")
	private LocalDateTime tstamp;
	
	@Column(name="trustLevel")
	private float trustLevel;
	
	@Column(name="status")
	private float status;
	
	@Column(name="behavior")
	private float behavior;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name="deviceId")
	@MapsId("deviceId")
	private Device device;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name="tmsId")
	@MapsId("tmsId")
	private PeerTms peerTms;
	
	
	public TrustAssessment() {
		
	}

	public TrustAssessment(float trustLevel, float status, float behavior) {
		this.trustLevel = trustLevel;
		this.status = status;
		this.behavior = behavior;
	}
	

	public float getTrustLevel() {
		return trustLevel;
	}

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

	public LocalDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(LocalDateTime tstamp) {
		this.tstamp = tstamp;
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

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public PeerTms getPeerTms() {
		return peerTms;
	}

	public void setPeerTms(PeerTms peerTms) {
		this.peerTms = peerTms;
	}
}
