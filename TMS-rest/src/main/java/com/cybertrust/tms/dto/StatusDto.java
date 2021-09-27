package com.cybertrust.tms.dto;

public class StatusDto {
	public static final String STATUS_OK = "OK";
	public static final String STATUS_ERROR = "ERROR";
	public static final String STATUS_UNKNOWN = "UNKNOWN";
	
	private String serviceStatus;
	private String serviceStatusInfo;
	private String dbStatus;
	private String dbStatusInfo;
	
	public StatusDto() {
		serviceStatus = serviceStatusInfo = dbStatus = dbStatusInfo = STATUS_UNKNOWN;
	}
	
   public StatusDto setServiceOK() {
	   this.serviceStatus = STATUS_OK;
	   this.serviceStatusInfo = STATUS_OK;
	   return this;
   }

   public StatusDto setServiceNotOK(String description) {
	   this.serviceStatus = STATUS_ERROR;
	   this.serviceStatusInfo = description;
	   return this;
   }   

   public StatusDto setDbOK() {
	   this.dbStatus = STATUS_OK;
	   this.dbStatusInfo = STATUS_OK;
	   return this;
   }

   public StatusDto setDbNotOK(String description) {
	   this.dbStatus = STATUS_ERROR;
	   this.dbStatusInfo = description;
	   return this;
   } 
   
   /**
	 * @return the serviceStatus
	 */
	public String getServiceStatus() {
		return serviceStatus;
	}

	/**
	 * @return the serviceStatusInfo
	 */
	public String getServiceStatusInfo() {
		return serviceStatusInfo;
	}

	/**
	 * @return the dbStatus
	 */
	public String getDbStatus() {
		return dbStatus;
	}

	/**
	 * @return the dbStatusInfo
	 */
	public String getDbStatusInfo() {
		return dbStatusInfo;
	}


}
