package com.cybertrust.tmslistener.restclient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.cybertrust.authentication.AuthClient;
import com.cybertrust.tmslistener.entity.Device;

public class RestClient {
	
	private static final String REST_URI
	 = "http://localhost:8080/tms-rest/device";
	
	private Client client = ClientBuilder.newClient();
	
	public Device getDevice(int id) {
		// added auth header
		return client.target(REST_URI).path(String.valueOf(id)).request(MediaType.APPLICATION_JSON).
				header(HttpHeaders.AUTHORIZATION, AuthClient.getAuthTokenType() + " " + AuthClient.getAuthToken()).
				get(Device.class);
		
	}

}
