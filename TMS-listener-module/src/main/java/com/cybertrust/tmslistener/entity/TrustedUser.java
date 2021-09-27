package com.cybertrust.tmslistener.entity;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="trusteduser")
public class TrustedUser {
	
	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="trustedUserId", unique=true, length=36)
	private String trustedUserId;
	
	@Column(name="description")
	private String description;
	
	@Column(name="trustLevel")
	private float trustLevel;
	
	@Column(name="extraInfo")
	private String extraInfo;
	
	@OneToMany(mappedBy="user",
			cascade= {CascadeType.PERSIST, CascadeType.DETACH,
						CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	private List<Device> user_devices;
	
	public TrustedUser () {
		
	}

	public TrustedUser(String trustedUserId, String description, float trustLevel, String extraInfo) {
		this.trustedUserId = trustedUserId;
		this.description = description;
		this.trustLevel = trustLevel;
		this.extraInfo = extraInfo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTrustedUserId() {
		return trustedUserId;
	}

	public void setTrustedUserId(String trustedUserId) {
		this.trustedUserId = trustedUserId;
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

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	public List<Device> getUser_devices() {
		return user_devices;
	}

	public void setUser_devices(List<Device> user_devices) {
		this.user_devices = user_devices;
	}

	public void add(Device device) {
		
		if (user_devices == null)
			user_devices = new ArrayList<>();
		
		user_devices.add(device);
		
		device.setUser(this);
		
	}
	
}
