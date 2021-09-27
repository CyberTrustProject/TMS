package com.cybertrust.tms.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cybertrust.tms.entity.TrustAssessment;
import com.cybertrust.tms.entity.TrustAssessmentId;

@Repository
public class TrustAssessmentDAOImpl implements TrustAssessmentDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public TrustAssessment getTrustAssessment(TrustAssessmentId id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		return currentSession.get(TrustAssessment.class, id);
		
	}

	@Override
	public List<TrustAssessment> getTrustAssessments() {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query<TrustAssessment> query = currentSession.createQuery("from TrustAssessment order by id", TrustAssessment.class);
		
		List<TrustAssessment> trustAssessments = query.getResultList();
		
		return trustAssessments;
		
	}

	@Override
	public void saveTrustAssessment(TrustAssessment trustAssessment) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		currentSession.save(trustAssessment);

	}

	@Override
	public void deleteTrustAssessment(TrustAssessmentId id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		TrustAssessment trustAssessment = currentSession.get(TrustAssessment.class, id);
		currentSession.delete(trustAssessment);

	}

}
