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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.TrustedUser;

@EnableTransactionManagement
@Repository
public class TrustedUserDAOImpl implements TrustedUserDAO {
	
	private static TrustedUserDAOImpl theTrustedUserDAOImpl = null;
	
	@Autowired
	TrustedUserDAOImpl() {
		theTrustedUserDAOImpl = this;
	}
	
	public static TrustedUserDAOImpl getTrustedUserDAOImpl() {
		return theTrustedUserDAOImpl;
	}
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public TrustedUser getTrustedUser(int id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		TrustedUser user = currentSession.get(TrustedUser.class, id);
		
		return user;
	}
	
	@Override
	public TrustedUser getTrustedUserByTrustedUserId(String trustedUserId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
	    CriteriaQuery<TrustedUser> criteria = builder.createQuery(TrustedUser.class);
	    Root<TrustedUser> from = criteria.from(TrustedUser.class);
	    criteria.select(from);
	    criteria.where(builder.equal(from.get("trustedUserId"), trustedUserId));
	    TypedQuery<TrustedUser> typed = session.createQuery(criteria);
		
	    try {
	        return typed.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
		
	}

	@Override
	public List<TrustedUser> getTrustedUsers() {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query<TrustedUser> query = currentSession.createQuery("from TrustedUser order by id", TrustedUser.class);
		
		List<TrustedUser> users = query.getResultList();
		
		return users;
	}



	@Override
	public void saveTrustedUser(TrustedUser trustedUser) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(trustedUser);
		
	}

	@Override
	public void deleteTrustedUser(String id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery("delete from TrustedUser where trustedUserId=:id");
		query.setParameter("id", id);
		
		query.executeUpdate();
		
	}
	
	

}
