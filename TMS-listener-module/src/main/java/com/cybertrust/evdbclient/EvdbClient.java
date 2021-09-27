package com.cybertrust.evdbclient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpStatus;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.bytecode.stackmap.TypeData.ClassName;

public class EvdbClient {
	private static final String UriPropertyName = "cybertrust.evdbClient.uri";
	private static final String AuthKeyPropertyName = "cybertrust.evdbClient.authKey";

	private static String uri;
	private static String secret;
	static boolean isInitialized = false;
	
	private static String getPropOrException(Properties props, String propertyName) throws IllegalArgumentException {
		String result = props.getProperty(propertyName);
		if (result == null)
			throw new IllegalArgumentException(ClassName.class.getCanonicalName() + ": property " + propertyName + "not found in property list");
		return result;
	}
	
	public static void initClient(Properties props) {
		uri = getPropOrException(props, UriPropertyName);
		secret = getPropOrException(props, AuthKeyPropertyName);
		isInitialized = true;
	}
	
	public static VulnerabilityInfo getVulnerabilityInfo(String vulnerabilityId) throws IOException {
		if (!isInitialized) {
			throw new IllegalArgumentException(ClassName.class.getCanonicalName() + ": not properly  initialized");
		}
		
		VulnerabilityInfo result = null;

		
	    CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(uri);
	 
	    String json = "{\"returnFormat\":\"json\",\"page\":\"1\",\"limit\":\"1\",\"value\":\"" + vulnerabilityId + "\"}";
	    StringEntity entity = new StringEntity(json);
	    httpPost.setEntity(entity);
	    httpPost.addHeader("Authorization", secret);
	    httpPost.setHeader("Accept", "application/json");
	    httpPost.setHeader("Content-type", "application/json");
	 
	    CloseableHttpResponse response = client.execute(httpPost);
	    
	    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	    	client.close();	
	    	return null;
	    }
	    String jsonResult = EntityUtils.toString(response.getEntity());
	    client.close();
	    ObjectMapper mapper = new ObjectMapper();
	    try {
			Map<String,Object> map = mapper.readValue(jsonResult, Map.class);
			List<Object> responseElement = (List<Object>)(map.get("response"));
			Map<String, Object> firstElement = (Map<String, Object>)(responseElement.get(0));
			Map<String, Object> eventElement = (Map<String, Object>)(firstElement.get("Event"));
			// Now extract needed info
			String info = (String)eventElement.get("info");
			String uuid = (String)eventElement.get("uuid");
			List<Object> objectElement = (List<Object>)(eventElement.get("Object"));
			// iterate over objects, and within the attributes of the objects, to get vulnerability score and cvss string
			float vulnScoreVal = -1;
			String cvssString = null;
			for (Object singleObjectElement: objectElement) {
				Map<String, Object> soe = (Map<String, Object>)singleObjectElement;
				List<Object> attributeObject = (List<Object>)(soe.get("Attribute"));
				//TODO
				//The way this is implemented, if we receive a response in which
				//one or more singleObjectElement(s) contain a valid CVSS score but invalid CVSS vector
				//and vice versa, we could end up with a wrong pair of values.
				//Define two additional variables to perform this check before assigning the pair
				//of new values to vulnScoreVal and cvssString
				for (Object singleAttributeElement: attributeObject) {
					Map<String, Object> sae = (Map<String, Object>)singleAttributeElement;
					String attrType = (String)(sae.get("object_relation"));
					if ((attrType != null) && (attrType.equals("cvss-score"))) {
						String scoreValue = (String)(sae.get("value"));
						if(Float.valueOf(scoreValue)>0.0f && Float.valueOf(scoreValue)<=10.0f)
							vulnScoreVal = Float.valueOf(scoreValue);
					}
					else if ((attrType != null) && (attrType.equals("cvss-string")) && !(sae.get("value").equals("N/A"))) {
						cvssString = (String)(sae.get("value"));
					}
					
				}
			}
			
			result = new VulnerabilityInfo(info, uuid, vulnScoreVal, cvssString);
		} catch (Exception e) {
			System.err.println("could not parse response for vulnerability " + vulnerabilityId);
		}
		return result;
	}
}
