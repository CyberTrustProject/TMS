package com.cybertrust.tms.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.PeerTms;

@Repository
public class PeerTmsDAOImpl implements PeerTmsDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public PeerTms getPeerTms(int id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		//PeerTms peerTms = currentSession.get(PeerTms.class, id);
		
		return currentSession.get(PeerTms.class, id);
		
	}
	
	@Override
	public PeerTms getPeerTmsByPeerTmsId(String peerTmsId) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
	    CriteriaQuery<PeerTms> criteria = builder.createQuery(PeerTms.class);
	    Root<PeerTms> from = criteria.from(PeerTms.class);
	    criteria.select(from);
	    criteria.where(builder.equal(from.get("peerTmsId"), peerTmsId));
	    TypedQuery<PeerTms> typed = session.createQuery(criteria);
		
	    try {
	        return typed.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	@Override
	public List<PeerTms> getPeerTmsSome(List<String> theIds) {
		Session currentSession = sessionFactory.getCurrentSession();
		String queryStr = "from PeerTms where peerTmsId in (";
		boolean addComma = false;
		for (String id0: theIds) {
			queryStr += ((addComma) ? (", ") : ("")) + "'" + id0 + "'";
			addComma = true;
		}
		queryStr += ")";
		Query <PeerTms> query = currentSession.createQuery(queryStr, PeerTms.class);
		return query.getResultList();

	}
	
	@Override
	public List<PeerTms> getPeerTmsAll() {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query<PeerTms> query = currentSession.createQuery("from PeerTms order by id", PeerTms.class);
		
		//List<PeerTms> peerTmsList = query.getResultList(); 
		
		return query.getResultList();
	}

	@Override
	public void savePeerTms(PeerTms peerTms) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		currentSession.save(peerTms);

	}

	@Override
	public void updatePeerTms(PeerTms peerTms) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		currentSession.saveOrUpdate(peerTms);
		
	}

	@Override
	public void deletePeerTms(String peerTmsId) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query query = currentSession.createQuery("delete from PeerTms where peerTmsId=:peerTmsId");
		
		query.setParameter("peerTmsId", peerTmsId);
		
		query.executeUpdate();

	}

}
