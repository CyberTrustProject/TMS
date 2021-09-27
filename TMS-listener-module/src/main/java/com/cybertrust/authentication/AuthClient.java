package com.cybertrust.authentication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import javassist.bytecode.stackmap.TypeData.ClassName;

public class AuthClient {
	
	private static final String UriPropertyName = "cybertrust.authClient.uri";
	private static final String GrantTypePropertyName = "cybertrust.authClient.grantType";
	private static final String ClientIdPropertyName = "cybertrust.authClient.clientId";
	private static final String ClientSecretPropertyName = "cybertrust.authClient.clientSecret";
	private static final String ScopePropertyName = "cybertrust.authClient.scope";
	
	private static final int RenewTokenSafetyMargin = 10; // renew the token that many seconds before it expires
	private static final int SecondsToRetryFailedRenewals = 5; // if a renew attempt fails, retry after that many seconds
	private static final int MinimunRefreshPeriodPeriod = 1; // the minimum refresh period
	
	private static AuthToken authToken = null;
	private static HttpPost postReq;
	private static boolean isInitialized = false;
	private static TokenRenewer tokenRenewer = null;

	private static String getPropOrException(Properties props, String propertyName) throws IllegalArgumentException {
		String result = props.getProperty(propertyName);
		if (result == null)
			throw new IllegalArgumentException(ClassName.class.getCanonicalName() + ": property " + propertyName + "not found in property list");
		return result;
	}
	
	public static void initClient(Properties props) throws IllegalArgumentException, URISyntaxException, ClientProtocolException, IOException {
		String authEndPoint1Str = getPropOrException(props, UriPropertyName);
		String grant_type1 = getPropOrException(props, GrantTypePropertyName);
		String client_id1 = getPropOrException(props, ClientIdPropertyName);
		String client_secret1 = getPropOrException(props, ClientSecretPropertyName);
		String scope1 = getPropOrException(props, ScopePropertyName);
		URI uri1 = new URI(authEndPoint1Str);
		
		HttpPost postReq1 = new HttpPost();
		List<NameValuePair> params = new ArrayList<NameValuePair>(4);
		params.add(new BasicNameValuePair("grant_type", grant_type1));
		params.add(new BasicNameValuePair("client_id", client_id1));
		params.add(new BasicNameValuePair("client_secret", client_secret1));
		params.add(new BasicNameValuePair("scope", scope1));
		postReq1.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		postReq1.setURI(uri1);
		fetchToken(postReq1);
		postReq = postReq1;
		isInitialized = true;
		if (tokenRenewer != null) { // clean up previous renewer, if any
			tokenRenewer.interrupt();
		}
		tokenRenewer = new TokenRenewer(authToken.getExpires_in());
		tokenRenewer.start();
	}

	public static String getAuthToken() {
		if (! isInitialized)
			return null;
		return authToken.getAccess_token();
	};
	
	public static String getAuthTokenType() {
		if (! isInitialized)
			return null;
		return authToken.getToken_type();		
	}
	
	private static void fetchToken(HttpPost postReq) throws ClientProtocolException, IOException {
		AuthToken at1 = new AuthToken(postReq);
		// Correctly fetched
		authToken = at1;
		// System.out.println("Fetched token" + at1.getAccess_token());
	}
	
	private static class TokenRenewer extends Thread {
		int sleepTimeoutMilis;
		
		TokenRenewer(int renewalPeriod) {
			setSleepTimeoutOnSuccess(renewalPeriod); // initially, renew after the specified token expiration period
		}
		
		public void run() {
			while (true) {
				try {
					Thread.sleep(sleepTimeoutMilis);
					fetchToken(postReq);
					setSleepTimeoutOnSuccess(authToken.getExpires_in()); // initially, renew after the specified token expiration period
				} catch (InterruptedException e) {
					// terminate thread
					this.interrupt();
				}
				catch (Exception e) { // any other exception, retry after secondsToRetryFailedRenewals
					e.printStackTrace();
					setSleepTimeoutOnFailure();
				}
			}
		}
		
		private void setSleepTimeoutOnSuccess(int secondsToWait) {
			int sleepTimeoutSecs = secondsToWait - RenewTokenSafetyMargin;
			if (sleepTimeoutSecs < MinimunRefreshPeriodPeriod) {
				sleepTimeoutSecs = MinimunRefreshPeriodPeriod;
			}
			sleepTimeoutMilis = sleepTimeoutSecs * 1000;
		}
		private void setSleepTimeoutOnFailure() {
			int sleepTimeoutSecs = SecondsToRetryFailedRenewals;
			if (sleepTimeoutSecs < MinimunRefreshPeriodPeriod) {
				sleepTimeoutSecs = MinimunRefreshPeriodPeriod;
			}
			sleepTimeoutMilis = sleepTimeoutSecs * 1000;
		}
	}
	
	
}
