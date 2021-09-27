package com.cybertrust.tms.dto;

public class TrustDimensionScore {
	
	private String dimension;
	
	private float level;

	public TrustDimensionScore(String dimension, float level) {
		this.dimension = dimension;
		this.level = level;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public float getLevel() {
		return level;
	}

	public void setLevel(float level) {
		this.level = level;
	}

}
