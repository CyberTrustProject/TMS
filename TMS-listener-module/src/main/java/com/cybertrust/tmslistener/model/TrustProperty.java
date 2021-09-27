package com.cybertrust.tmslistener.model;

import java.util.Properties;

public class TrustProperty {
	
	private static float statusBasedTrustWeight;
	
	private static float behaviorBasedTrustWeight;
	
	private static float riskBasedTrustWeight;
	
	private static float nonCompliantTrafficPenalty;
	
	private static float attackLaunchPenalty;
	
	private static float deviationFromNominalMetricsPenalty;
	
	public static void init(Properties props) {
		
		statusBasedTrustWeight = Float.parseFloat(props.getProperty("com.cybertrust.tms.statusBasedTrustWeight"));
		
		behaviorBasedTrustWeight = Float.parseFloat(props.getProperty("com.cybertrust.tms.behaviorBasedTrustWeight"));
		
		riskBasedTrustWeight = Float.parseFloat(props.getProperty("com.cybertrust.tms.riskBasedTrustWeight"));
		
		nonCompliantTrafficPenalty = Float.parseFloat(props.getProperty("com.cybertrust.tms.nonCompliantTrafficPenalty"));
		
		attackLaunchPenalty = Float.parseFloat(props.getProperty("com.cybertrust.tms.attackLaunchPenalty"));
		
		deviationFromNominalMetricsPenalty = Float.parseFloat(props.getProperty("com.cybertrust.tms.deviationFromNominalMetricsPenalty"));
		
	}

	public static float getStatusBasedTrustWeight() {
		return statusBasedTrustWeight;
	}

	public static void setStatusBasedTrustWeight(float statusBasedTrustWeight) {
		TrustProperty.statusBasedTrustWeight = statusBasedTrustWeight;
	}

	public static float getBehaviorBasedTrustWeight() {
		return behaviorBasedTrustWeight;
	}

	public static void setBehaviorBasedTrustWeight(float behaviorBasedTrustWeight) {
		TrustProperty.behaviorBasedTrustWeight = behaviorBasedTrustWeight;
	}
	
	public static float getRiskBasedTrustWeight() {
		return riskBasedTrustWeight;
	}

	public static void setRiskBasedTrustWeight(float riskBasedTrustWeight) {
		TrustProperty.riskBasedTrustWeight = riskBasedTrustWeight;
	}

	public static float getNonCompliantTrafficPenalty() {
		return nonCompliantTrafficPenalty;
	}

	public static void setNonCompliantTrafficPenalty(float nonCompliantTrafficPenalty) {
		TrustProperty.nonCompliantTrafficPenalty = nonCompliantTrafficPenalty;
	}

	public static float getAttackLaunchPenalty() {
		return attackLaunchPenalty;
	}

	public static void setAttackLaunchPenalty(float attackLaunchPenalty) {
		TrustProperty.attackLaunchPenalty = attackLaunchPenalty;
	}

	public static float getDeviationFromNominalMetricsPenalty() {
		return deviationFromNominalMetricsPenalty;
	}

	public static void setDeviationFromNominalMetricsPenalty(float deviationFromNominalMetricsPenalty) {
		TrustProperty.deviationFromNominalMetricsPenalty = deviationFromNominalMetricsPenalty;
	}

}
