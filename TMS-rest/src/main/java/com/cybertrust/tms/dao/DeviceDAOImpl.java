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

import com.cybertrust.tms.entity.Device;

@EnableTransactionManagement
@Repository
public class DeviceDAOImpl implements DeviceDAO {
	
	@Autowired
	private SessionFactory sessionFactory;


	@Override
	public Device getDevice(int id) {	
		Session currentSession = sessionFactory.getCurrentSession();
		return currentSession.get(Device.class, id);
	}
	
	@Override
	public Device getDeviceByDeviceId(String deviceId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
	    CriteriaQuery<Device> criteria = builder.createQuery(Device.class);
	    Root<Device> from = criteria.from(Device.class);
	    criteria.select(from);
	    criteria.where(builder.equal(from.get("deviceId"), deviceId));
	    TypedQuery<Device> typed = session.createQuery(criteria);
		
	    try {
	        return typed.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	@Override
	public List<Device> getDevices() {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query<Device> query = currentSession.createQuery("from Device order by id", Device.class);
		
		List<Device> devices = query.getResultList(); 
		
		return devices;
	}

	@Override
	public void saveDevice(Device device) {
	
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(device);
		currentSession.flush();
	}
	
	@Override
	public void createDevice(Device device) {
	
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(device);
		currentSession.flush();
	}
	

	@Override
	public void deleteDevice(int id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query query = currentSession.createQuery("delete from Device where id=:deviceId");
		
		query.setParameter("deviceId", id);
		
		query.executeUpdate();

	}

}
