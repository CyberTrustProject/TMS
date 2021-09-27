package com.cybertrust.tms.dto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.SessionFactory;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.cybertrust.tms.dao.DeviceDAOImpl;
import com.cybertrust.tms.dao.TrustedUserDAOImpl;
import com.cybertrust.tms.entity.TrustedUser;

@Component
//@ApplicationScope
public class ReferenceMapper {

	@PersistenceContext
	private EntityManager entityManager;
	
	public TrustedUser resolve(int id, @TargetType Class<TrustedUser> entityClass) {
		TrustedUserDAOImpl myTrustedUserDAOImpl = TrustedUserDAOImpl.getTrustedUserDAOImpl();
        return (myTrustedUserDAOImpl.getTrustedUser(id));
    }
    public int toReference(TrustedUser tu) {
        return (tu != null) ? (tu.getId()) : -1;
    }
}
	