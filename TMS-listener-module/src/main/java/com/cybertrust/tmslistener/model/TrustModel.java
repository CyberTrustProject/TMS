package com.cybertrust.tmslistener.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cybertrust.evdbclient.EvdbClient;
import com.cybertrust.evdbclient.VulnerabilityInfo;

public class TrustModel {
	
	private static int maxConcurrentThreads; // not strictly part of the model but pertains to its execution; merged here for convenience

	private static float statusBasedTrustWeight;

	private static float behaviorBasedTrustWeight;

	private static float associatedRiskBasedTrustWeight;
	
	private static float saf;
	
	private static float af;
	
	private static float complianceRestorationFactor;
	
	private static float nominalityRestorationFactor;
	
	private static float lowerTrustThreshold;
	
	private static float upperTrustThreshold;
	
	private static float LWmin;
	
	private static float ptThreshold;
	
	private static float trustedUserDefault;

	private static final float[][] fuzzy = 	{ 	{1, 1, 1, 0.75f, 0.5f}, 
												{1, 1, 0.75f, 0.5f, 0.25f}, 
												{1, 0.75f, 0.5f, 0.25f, 0}, 
												{0.75f, 0.5f, 0.25f, 0, 0}, 
												{0.5f, 0.25f, 0, 0, 0} 
											};
	
	private static final HashMap<String, Integer> explicitImpactIndex = new HashMap<String,Integer>() {{
	    put("Catastrophic", 0);
	    put("Severe", 1);
	    put("Normal", 2);
	    put("Minor", 3);
	    put("Negligible", 4);
	}};

	public TrustModel() {

	}

	public static void init(Properties props) {
		statusBasedTrustWeight = Float.parseFloat(props.getProperty("com.cybertrust.tms.statusBasedTrustWeight"));
		behaviorBasedTrustWeight = Float.parseFloat(props.getProperty("com.cybertrust.tms.behaviorBasedTrustWeight"));
		associatedRiskBasedTrustWeight = Float.parseFloat(props.getProperty("com.cybertrust.tms.associatedRiskBasedTrustWeight"));
		saf = Float.parseFloat(props.getProperty("com.cybertrust.tms.vulnerabilitiesAmortizationFactor"));
		af = Float.parseFloat(props.getProperty("com.cybertrust.tms.neighborsAmortizationConstant"));
		complianceRestorationFactor = Float.parseFloat(props.getProperty("com.cybertrust.tms.complianceRestorationFactor"));
		nominalityRestorationFactor = Float.parseFloat(props.getProperty("com.cybertrust.tms.nominalityRestorationFactor"));
		maxConcurrentThreads = Integer.parseInt(props.getProperty("com.cybertrust.tms.maxConcurrentThreads"));
		lowerTrustThreshold = Float.parseFloat(props.getProperty("com.cybertrust.tms.lowerTrustThreshold"));
		upperTrustThreshold = Float.parseFloat(props.getProperty("com.cybertrust.tms.upperTrustThreshold"));
		LWmin = Float.parseFloat(props.getProperty("com.cybertrust.tms.LWmin"));
		ptThreshold = Float.parseFloat(props.getProperty("com.cybertrust.tms.ptThreshold"));
		trustedUserDefault = Float.parseFloat(props.getProperty("TrustLevel.User.Default"));

	}

	public static int getMaxConcurrentThreads() {
		return maxConcurrentThreads;
	}
	
	public static float getLowerTrustThreshold() {
		return lowerTrustThreshold;
	}

	public static float getUpperTrustThreshold() {
		return upperTrustThreshold;
	}
	
	public static float getTrustedUserDefault() {
		return trustedUserDefault;
	}

	//adopting the simple additive weighted model for now
	public static float localTrust(float status, float behavior, float risk) {

		return status*statusBasedTrustWeight + behavior*behaviorBasedTrustWeight + risk*associatedRiskBasedTrustWeight;

	}

	public static float statusTrust(boolean integrity, float vulnerabilities) {

		if(integrity == false)
			return 0;
		
		float ovim = vulnerabilities;
		
		if(ovim == 0)
			return 1;
		
		return (float) (1 - Math.exp(-1*ovim*saf));
	}
	
	public static float vulnerabilitiesStatusTrust(ArrayList<HashMap<String, String>> vulnerabilities) {
		
		float ovim = 0;
		VulnerabilityInfo vulnerInfo;

		for(HashMap<String, String> vuln : vulnerabilities) {
			try {
				vulnerInfo = EvdbClient.getVulnerabilityInfo(vuln.get("cve"));
				if(vulnerInfo.getCvssScore()!=-1 && vulnerInfo.getCvssString()!=null && vulnerInfo.isRemotelyExploitable())
					ovim += Float.parseFloat(vuln.get("cvssScore"))/10;
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
		}
		return ovim;
		
	}
	
	public static float behaviorTrust(float compliance, float nominality,  boolean maliciousOrNonCompliant) {
		
		if(maliciousOrNonCompliant == true)
			return 0;
		
		return nominality * compliance;
		
	}
	
	public static float nominalityTrust(float nominality, float detectedMaxMetricValue, float nominalRangeHighEnd) {
		if (detectedMaxMetricValue > nominalRangeHighEnd)
			nominality -= nominality * ((detectedMaxMetricValue - nominalRangeHighEnd)/detectedMaxMetricValue);
		
		if (nominality < 0)
			nominality = 0;
		else if (nominality > 1)
			nominality = 1;
		
		return nominality;
		
	}
	
	public static float restoreTrust(float restore, String aspect) {
		
		if(restore == 0)
			return 0.05f;
		
		else if(aspect.equals("compliance"))
			return ((restore += restore*complianceRestorationFactor) > 1) ? 1 : restore;
		
		else if (aspect.equals("nominality"))
			return ((restore += restore*nominalityRestorationFactor) > 1) ? 1 : restore;
		
		return 0;
	}
	
	public static float associatedRiskTrust(float singularRisk, float compromiseRisk, float neighborSingularRisks) {
		
		float ccen = neighborSingularRisks;
		float accen = compromiseRisk* (float) (1 - Math.exp(-1*(double) ccen*af));
		
		return 1 - ((1-singularRisk) * (1 - accen));
		
	}
	
	public static float singularRiskTrust(String explicitImpact, float compromiseRisk) {
		
		float singularRisk;
		
		if(explicitImpact == null)
			return compromiseRisk;
		
		if(compromiseRisk > 0.85f)
			singularRisk = fuzzy[explicitImpactIndex.get(explicitImpact)][0];
		else if(compromiseRisk > 0.6f)
			singularRisk = fuzzy[explicitImpactIndex.get(explicitImpact)][1];
		else if(compromiseRisk > 0.3f)
			singularRisk = fuzzy[explicitImpactIndex.get(explicitImpact)][2];
		else if(compromiseRisk > 0.1f)
			singularRisk = fuzzy[explicitImpactIndex.get(explicitImpact)][3];
		else
			singularRisk = fuzzy[explicitImpactIndex.get(explicitImpact)][4];
		
		return singularRisk;
		
	}
	
	public static float peerTrustAssessment(float[][] trustValues) {
		
		float ETL;
		float sumETL = 0;
		float dividendPTA = 0;
		
		for(int i=0; i<trustValues.length; i++) {
			
			ETL = trustValues[i][1] * trustValues[i][2];
			
			dividendPTA += trustValues[i][0] * ETL;
			
			sumETL += ETL;
			
		}
		
		if(sumETL > 0) {
			return dividendPTA / sumETL;
		}
		else
			return 0;
		
	}
	
	public static float localTrustWeight(float[][] trustValues) {
		
		float LW;
		float sumETL = 0;
		
		for(int i=0; i<trustValues.length; i++)
			sumETL += trustValues[i][1] * trustValues[i][2];
		
		if(sumETL >= ptThreshold)
			LW=LWmin;
		else {
			LW = (((1-LWmin) * sumETL) / ptThreshold) + LWmin; 
		}
		
		return LW;
		
	}
	
	//May be redundant.
	//Implemented it because the sumETL(aka cumulativePeerTmsTrust) was requested as a property of Device.
	public static float sumETL(float [][] trustValues) {
		float sumETL = 0;
		
		for(int i=0; i<trustValues.length; i++)
			sumETL += trustValues[i][1] * trustValues[i][2];
		
		return sumETL;
	}
	
	public static float communityTrust(float LW, float localTrust, float peerTrust) {
		
		return LW * localTrust + (1-LW) * peerTrust;
		
	}

}
