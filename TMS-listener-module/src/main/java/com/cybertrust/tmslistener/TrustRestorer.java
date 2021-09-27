package com.cybertrust.tmslistener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cybertrust.tmslistener.config.HibernateUtil;
import com.cybertrust.tmslistener.dao.DeviceDAO;
import com.cybertrust.tmslistener.entity.Device;
import com.cybertrust.tmslistener.model.TrustModel;
import com.cybertrust.tmslistener.restclient.RestClient;

public class TrustRestorer extends Thread {
	int restoringPeriod; // delay between trust restoration periods in seconds
	float restorationFactor;
	private RestClient client = new RestClient();

	TrustRestorer(int restoringPeriod, float restorationFactor) {
		this.restoringPeriod = restoringPeriod;
		this.restorationFactor = restorationFactor;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep((long)restoringPeriod * 10000);
			} catch (InterruptedException e) {
				System.err.println("Trust restorer: wait interrupted, sleeping for next restore cycle");
				e.printStackTrace();
				continue;
			}

			float restoredCompliance;
			float restoredNominality;
			float updatedBehaviorTrust;

			Transaction transact = null;

			try (Session session = HibernateUtil.getSession();) {
				transact = session.beginTransaction();
				try {
					List<Device> updatableDevices = DeviceDAO.getRestorableDevices(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
					        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()).minusSeconds(restoringPeriod), session);
					for (Device device: updatableDevices) {

						float initialCompliance = device.getCompliance();
						float initialNominality = device.getNominality();
						float initialBehaviorTrust = device.getBehavior();
						
						restoredCompliance = TrustModel.restoreTrust(device.getCompliance(), "compliance");
						restoredNominality = TrustModel.restoreTrust(device.getNominality(), "nominality");
						updatedBehaviorTrust = TrustModel.behaviorTrust(restoredCompliance, restoredNominality, device.getMalicious()); 

						if ((initialCompliance != restoredCompliance) || (initialNominality != restoredNominality) || (initialBehaviorTrust != updatedBehaviorTrust)) {	
							device.setCompliance(restoredCompliance);
							device.setNominality(restoredNominality);
							
							DeviceDAO.updateDeviceTrust(device, device.getStatus(), updatedBehaviorTrust, device.getAssociatedRisk(), "trustRestoration", session);
						}
					}
					transact.commit();
				}
				catch (Exception e) {
					System.err.println("Trust restorer: Error in updating trust levels, aborting transaction");
					transact.rollback();
					e.printStackTrace();
				}
			}
			catch (Exception e) {
				System.err.println("Trust restorer: Cannot establish connection to the database");
				e.printStackTrace();
			}
		}
	}
}