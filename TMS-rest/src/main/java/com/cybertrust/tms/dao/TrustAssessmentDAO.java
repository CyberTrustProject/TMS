package com.cybertrust.tms.dao;

import java.util.List;

import com.cybertrust.tms.entity.TrustAssessment;
import com.cybertrust.tms.entity.TrustAssessmentId;

public interface TrustAssessmentDAO {
	
	public TrustAssessment getTrustAssessment(TrustAssessmentId id);
	
	public List<TrustAssessment> getTrustAssessments();
	
	public void saveTrustAssessment(TrustAssessment trustAssessment);
	
	public void deleteTrustAssessment(TrustAssessmentId id);

}
