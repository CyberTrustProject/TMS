package com.cybertrust.tmslistener.dao;


import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.cybertrust.tmslistener.config.HibernateUtil;
import com.cybertrust.tmslistener.entity.Device;
import com.cybertrust.tmslistener.entity.TrustedUser;
import com.cybertrust.tmslistener.model.TrustModel;

public class TrustedUserDAO {


	public static TrustedUser getTrustedUser(int id, Session session) {

		return session.get(TrustedUser.class, id);

	}

	public static TrustedUser getTrustedUserByTrustedUserId(String trustedUserId, Session session) {

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<TrustedUser> criteria = builder.createQuery(TrustedUser.class);
		Root<TrustedUser> from = criteria.from(TrustedUser.class);
		criteria.select(from);
		criteria.where(builder.equal(from.get("trustedUserId"), trustedUserId));
		TypedQuery<TrustedUser> typed = session.createQuery(criteria);

		try {
			TrustedUser trustedUser = typed.getSingleResult();
			return trustedUser;
		} catch (NoResultException e) {
			return null;
		}

	}
	

	public static float getUserTrust(int id, Session session) {

		return session.get(TrustedUser.class, id).getTrustLevel();

	}

	public static TrustedUser registerUser(String trustedUserId, Session session) {

		TrustedUser user = new TrustedUser();
		user.setTrustedUserId(trustedUserId);
		user.setTrustLevel(TrustModel.getTrustedUserDefault());
		session.save(user);

		return user;

	}

	public static TrustedUser registerUser(String trustedUserId, String trustedUserDescription, Session session) {

		TrustedUser user = new TrustedUser();
		user.setTrustedUserId(trustedUserId);
		user.setDescription(trustedUserDescription);
		user.setTrustLevel(TrustModel.getTrustedUserDefault());
		session.save(user);

		return user;

	}	
	

}
