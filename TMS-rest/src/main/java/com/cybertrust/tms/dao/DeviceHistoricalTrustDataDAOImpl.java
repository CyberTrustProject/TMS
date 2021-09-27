package com.cybertrust.tms.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cybertrust.tms.entity.DeviceHistoricalTrustData;
import com.cybertrust.tms.entity.DeviceHistoricalTrustDataId;

@Repository
public class DeviceHistoricalTrustDataDAOImpl implements DeviceHistoricalTrustDataDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public DeviceHistoricalTrustData getDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		return currentSession.get(DeviceHistoricalTrustData.class, id);
		
	}

	@Override
	public List<DeviceHistoricalTrustData> getDeviceHistoricalTrustDataList() {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query<DeviceHistoricalTrustData> query = currentSession.createQuery("from DeviceHistoricalTrustData order by id", DeviceHistoricalTrustData.class);
		
		List<DeviceHistoricalTrustData> deviceHistorical = query.getResultList();
		
		return deviceHistorical;
		
	}

	@Override
	public void saveDeviceHistoricalTrustData(DeviceHistoricalTrustData deviceHistorical) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		currentSession.save(deviceHistorical);

	}

	@Override
	public void deleteDeviceHistoricalTrustData(DeviceHistoricalTrustDataId id) {
		
		Session currentSession = sessionFactory.getCurrentSession();
		
		DeviceHistoricalTrustData deviceHistorical = currentSession.get(DeviceHistoricalTrustData.class, id);
		currentSession.delete(deviceHistorical);

	}

}
