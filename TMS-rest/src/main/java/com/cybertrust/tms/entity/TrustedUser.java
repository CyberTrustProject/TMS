package com.cybertrust.tms.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.cybertrust.tms.rest.Views;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="trusteduser")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class TrustedUser {
	
	@Id
	@Column(name="id")
	@JsonView(Views.Internal.class)
	private int id;
	
	@Column(name="trustedUserId", unique=true, length=36)
	@JsonView(Views.Public.class)
	private String trustedUserId;
	
	@Column(name="description")
	@JsonView(Views.Public.class)
	private String description;
	
	@Column(name="trustLevel")
	@JsonView(Views.Public.class)
	private float trustLevel;
	
	@Column(name="extraInfo")
	@JsonView(Views.Public.class)
	private String extraInfo;
	
	@OneToMany(mappedBy="user",
			cascade= {CascadeType.PERSIST, CascadeType.DETACH,
						CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	@JsonView(Views.Internal.class)
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

	public float getTrustLevel() {
		return trustLevel;
	}

	public void setTrustLevel(float trustLevel) {
		this.trustLevel = trustLevel;
	}

	
	public List<Device> getUser_devices() {
		return user_devices;
	}

	public void setUser_devices(List<Device> user_devices) {
		this.user_devices = user_devices;
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

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public void add(Device device) {
		
		if (user_devices == null) {
			user_devices = new ArrayList<>();
		}
		
		user_devices.add(device);
		
		device.getUser();
		device.setUser(this);
		
	}

}
