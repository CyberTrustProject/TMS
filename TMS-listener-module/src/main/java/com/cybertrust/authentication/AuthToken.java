package com.cybertrust.authentication;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthToken {
	private String access_token = null;
	private int expires_in = 0;
	private String token_type = null;
	
	public static AuthToken createMessageFromJSON(String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		AuthToken am = objectMapper.readValue(json, AuthToken.class); 
		return am;
	}
	
	public AuthToken() {
		;
	}
	
	public AuthToken(HttpPost postReq) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = null;
		
		try {
			httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(postReq);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				postReq.releaseConnection();
				throw new IOException("null response retrieved from auth endpoint " + postReq.getURI());
			}
			String responseData = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(entity);
			postReq.releaseConnection();
			
			AuthToken am1 = AuthToken.createMessageFromJSON(responseData);
			this.access_token = am1.access_token;
			this.expires_in = am1.expires_in;
			this.token_type = am1.token_type;
		}
		catch (Exception e) {
			throw(e);
		}
		finally {
			if (httpclient != null)
				httpclient.close();
		}
	}
	
	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @param access_token the access_token to set
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * @return the expires_in
	 */
	public int getExpires_in() {
		return expires_in;
	}

	/**
	 * @param expires_in the expires_in to set
	 */
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	/**
	 * @return the token_type
	 */
	public String getToken_type() {
		return token_type;
	}

	/**
	 * @param token_type the token_type to set
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	
}
