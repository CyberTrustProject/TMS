package com.cybertrust.tms.dao;

import java.util.List;

import com.cybertrust.tms.entity.Device;

public interface RiskDAO {
	
	public List<Device> getPrioritizedRisks(int numRisks);

}
