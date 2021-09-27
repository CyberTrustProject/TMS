package com.cybertrust.tms.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cybertrust.tms.entity.Device;

@Repository
public class RiskDAOImpl implements RiskDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Device> getPrioritizedRisks(int numRisks) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
	    CriteriaQuery<Device> criteria = builder.createQuery(Device.class);
	    Root<Device> from = criteria.from(Device.class);
	    criteria.select(from);
	    criteria.orderBy(builder.asc(from.get("associatedRisk")));
	    TypedQuery<Device> typed = session.createQuery(criteria);
		
	    try {
	        return typed.setFirstResult(0).setMaxResults(numRisks).getResultList();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

}
