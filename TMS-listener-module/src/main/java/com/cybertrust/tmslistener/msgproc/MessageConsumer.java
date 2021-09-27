package com.cybertrust.tmslistener.msgproc;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cybertrust.busmessages.GenericMessage;
import com.cybertrust.evdbclient.EvdbClient;
import com.cybertrust.tmslistener.Config;
import com.cybertrust.tmslistener.config.HibernateUtil;
import com.cybertrust.tmslistener.dao.DeviceDAO;
import com.cybertrust.tmslistener.dao.TrustedUserDAO;
import com.cybertrust.tmslistener.entity.Device;
import com.cybertrust.tmslistener.entity.TrustedUser;
import com.cybertrust.tmslistener.model.TrustModel;
import com.cybertrust.txmgmt.TransactionManager;
import com.cybertrust.txmgmt.TransactionManager.TransactionCallable;
import com.fasterxml.jackson.core.JsonProcessingException;


public class MessageConsumer implements MessageHandler {

	private String consumerName;
	private static int MAX_NO_OF_THREADS;
	private static Semaphore concurrencySemaphore;
	private static boolean concurrencyIsInitialized = false;

	public static void setConcurrencyLevel(int concurrencyLevel) {
		if (concurrencyIsInitialized) {
			throw new IllegalStateException("MessageConsumer: concurrency level already initialized");
		}
		MAX_NO_OF_THREADS = concurrencyLevel;
		concurrencySemaphore = new Semaphore(MAX_NO_OF_THREADS);
		concurrencyIsInitialized = true;
	}

	public MessageConsumer(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getName() {return consumerName;}

	public void onMessage(Message message) {
		// arrange for at most MAX_NOF_THREADS concurrent executions
		try {
			if (!concurrencyIsInitialized) {
				setConcurrencyLevel(1); // default concurrency level
			}
			concurrencySemaphore.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String messageText = "";

		LocalDateTime now = LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
				.appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter());

		try {
			if (message instanceof ActiveMQTextMessage) {
				TextMessage textMessage = (TextMessage) message;
				messageText = textMessage.getText();
			}
			else {
				BytesMessage bm = (BytesMessage) message;
				byte data[] = new byte[(int) bm.getBodyLength()];
				bm.readBytes(data);
				messageText = new String(data);
			}

			// check if we need to get rid of the first dot-separated component for further comparisons. This happens if the 
			// first dot-separated component is a number
			String soholessTopic, firstPart, secondPart;
			int dotPosition;
			
			dotPosition = consumerName.indexOf(".");
			if (dotPosition == -1) {
				soholessTopic = consumerName;
			}
			else {
				firstPart = consumerName.substring(0, dotPosition);
				soholessTopic = consumerName.substring(dotPosition + 1);
				if ((firstPart.length() > 0) && (soholessTopic.length() > 0)) {
					try {
						Integer testInt = Integer.parseInt(firstPart);
						// should it succeed, it's an integer, leave it chopped
					}
					catch (Exception e) {
						// not an integer, no chopping, so roll it back
						soholessTopic = consumerName;
					}
				}
				else
					soholessTopic = consumerName;
			}
			
			
			
			//Make sure all received messages are processed as JSON arrays
			if(!messageText.startsWith("["))
				messageText = "[" + messageText.concat("]");
			//The Network.Alert messages sent from UOPHEC are not in line with the sample payloads or with JSON format.
			//Some pre-processing is required.
			System.out.println(soholessTopic);
			if(consumerName.equals("Network.Alert") || consumerName.equals("5002.Network.Alert") || consumerName.equals("7246.Network.Alert") || (soholessTopic.equals("Network.Alert"))) {
				//For easier troubleshooting
				// System.out.println("\n" + now + " : " + consumerName + " received (RAW) " + messageText);
				//messageText = messageText.replace("}\"]", "}]").replace("}\"", "}").replace("\"{", "{").replace("\\", "");
				messageText = messageText.replace("\\", "");
				messageText = messageText.replace("}\"", "}").replace("\"{", "{");
			}
			//else
			//	messageText = "[" + messageText.concat("]");
			
			System.out.println("\n" + now + " : " + consumerName + " received (EDITED) " + messageText);
			
			boolean checkJson = isJSONValid(messageText);
			if(!checkJson)
				throw new org.json.JSONException("Invalid JSON Array");

			JSONArray array = new JSONArray(messageText);
			
			for(int u=0; u<array.length(); u++) {
				
				checkJson = isJSONValid(array.get(u).toString());
				if(!checkJson)
					continue;
				
				messageText= array.getJSONObject(u).toString();
				
				GenericMessage m1 = GenericMessage.createMessageFromJSON(messageText);

				boolean hasValidSignature = false;
				Exception signatureValidationException = null;
				try {
					hasValidSignature = m1.hasValidSignature();
				}
				catch (Exception e) {
					signatureValidationException = e; 
				}
				if (! hasValidSignature) {
					System.out.println("message " + m1.getMsgId() + " has no valid signature");
					if (signatureValidationException != null)
						System.out.println("Nested exception message is:" + signatureValidationException.getMessage());
				}

				if((hasValidSignature) || (Config.getInvalidMessageBehavior() == Config.InvalidMessageBehaviorWarning)) {
					String messageTopic = m1.getMsgTopic();
					Map<String, Object> payload = m1.getPayload();

					String offendingDevice;
					Device device;
					float status;
					float behavior;
					float updatedBehaviorTrust;
					Transaction transact = null;
					if ((messageTopic == null) || (messageTopic.equals(""))) {
						messageTopic = soholessTopic;
					}
					switch (messageTopic) {
					case "deviceNonComplianceInfo":
						try (Session session = HibernateUtil.getSession();) {
							transact = session.beginTransaction();
							try {
								offendingDevice = (String)(payload.get("deviceId"));
								device = DeviceDAO.getDeviceBy(offendingDevice, null, null, session);

								if (device == null) {
									System.out.println("noncompliance: device with id = " + offendingDevice + "not found; skipping...");
								}
								else {

									System.out.println("\nCurrent Trust: " + DeviceDAO.getDeviceTrust(device.getId(), session));
									System.out.println();

									device.setCompliance(0);
									updatedBehaviorTrust = TrustModel.behaviorTrust(0, 0, true);

									DeviceDAO.updateDeviceTrust(device, device.getStatus(), updatedBehaviorTrust, device.getAssociatedRisk(), messageTopic, session);

									transact.commit();

									System.out.println("\nRecalculated trust after deviceNonComplianceInfo message: " + DeviceDAO.getDeviceTrust(device.getId(), session));
									System.out.println();
								}
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								transact.rollback();
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}
						break;

					case "4036.Device.Alert":
					case "4034.Device.Alert":
					case "4060.Device.Alert":
					case "4063.Device.Alert":
					case "4046.Device.Alert":
					case "4029.Device.Alert":
					case "207030.Device.Alert":
					case "207027.Device.Alert":
					case "207028.Device.Alert":
					case "207029.Device.Alert":
					case "deviceDeviationInfo":
					case "Device.Alert":
					case "5002.Device.Alert":
					case "7246.Device.Alert":
						try (Session session = HibernateUtil.getSession();) {
							transact = session.beginTransaction();
							try {
								offendingDevice = (String)(payload.get("deviceId"));
								device = DeviceDAO.getDeviceBy(offendingDevice, null, null, session);
								if (device == null) {
									System.out.println("deviceDeviationInfo: device with id " + offendingDevice + " not found. Skipping...");
								}
								else {

									HashMap<String, String> deviationInfo = (HashMap<String,String>) payload.get("deviationInfo");
									System.out.println("\nCurrent Trust: " + device.getTrustLevel());
									System.out.println();

									float updatedNominality = TrustModel.nominalityTrust(device.getNominality(), 
											Float.parseFloat(deviationInfo.get("detectedMaxMetricValue")),
											Float.parseFloat(deviationInfo.get("nominalRangeHighEnd")));
											

									device.setNominality(updatedNominality);

									updatedBehaviorTrust = TrustModel.behaviorTrust(device.getCompliance(), updatedNominality, device.getMalicious());

									DeviceDAO.updateDeviceTrust(device, device.getStatus(), updatedBehaviorTrust, device.getAssociatedRisk(), messageTopic, session);
									transact.commit();

									System.out.println("\nRecalculated trust after deviceDeviationInfo message: " + DeviceDAO.getDeviceTrust(device.getId(), session));
									System.out.println();
								}
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								transact.rollback();
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}


						break;

					case "4036.Network.Alert":
					case "4034.Network.Alert":
					case "4060.Network.Alert":
					case "4063.Network.Alert":
					case "4046.Network.Alert":
					case "4029.Network.Alert":
					case "207030.Network.Alert":
					case "207027.Network.Alert":
					case "207028.Network.Alert":
					case "207029.Network.Alert":
					case "attackDetection":
					case "Network.Alert":
					case "5002.Network.Alert":
					case "7246.Network.Alert":
						try (Session session = HibernateUtil.getSession();) {
							transact = session.beginTransaction();
							try {
								JSONObject netalert = new JSONObject(messageText);

								if(!netalert.has("event_type") || !netalert.has("src_ip"))
									System.out.println("Network.Alert - Invalid Format: No event_type or src_ip field(s).");
								else if(netalert.getString("event_type").equals("") || netalert.getString("src_ip").equals("")) {
									System.out.println("Network.Alert - Invalid Format: event_type or src_ip field(s) empty");
								}

								else if(netalert.getString("event_type").equals("alert")) {

									device = DeviceDAO.getDeviceByAny(null, netalert.getString("src_ip"), session);

									if(device==null)
										System.out.println("Network.Alert - No device with ip: " + netalert.getString("src_ip"));

									else {
										device.setMalicious(true);
										updatedBehaviorTrust = TrustModel.behaviorTrust(0, 0, true);

										DeviceDAO.updateDeviceTrust(device, device.getStatus(), updatedBehaviorTrust, device.getAssociatedRisk(), messageTopic, session);
										transact.commit();
									}
								}
								else
									System.out.println("Network.Alert - event_type:"+ netalert.getString("event_type"));
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								transact.rollback();
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}

						break;


						//iIRS REST no 16.
					case "4036.Network.Risk":
					case "4034.Network.Risk":
					case "4060.Network.Risk":
					case "4063.Network.Risk":
					case "4046.Network.Risk":
					case "4029.Network.Risk":
					case "207030.Network.Risk":
					case "207027.Network.Risk":
					case "207028.Network.Risk":
					case "207029.Network.Risk":
					case "riskScore":
					case "5002.Network.Risk":
					case "7246.Network.Risk":
						try (Session session = HibernateUtil.getSession();) {
							transact = session.beginTransaction();
							try {
								JSONObject netrisk = new JSONObject(messageText);

								boolean msgOK = false;
								if(netrisk!=null) {
									netrisk=netrisk.getJSONObject("payload");
									if(netrisk!=null)
										msgOK=true;
								}

								if(!msgOK)
									System.out.println("Network.Risk message does not contain payload.hosts field");
								else {
									JSONArray netrisk_hosts = netrisk.getJSONArray("hosts");
									if(netrisk_hosts == null) {
										System.out.println("Network.Risk message does not contain payload.hosts field.");
									}
									else {
										String deviceId = null;
										String deviceIp = null;
										for(int i=0; i<netrisk_hosts.length(); i++) {

											if (netrisk_hosts.getJSONObject(i).has("id"))
												deviceId = netrisk_hosts.getJSONObject(i).getString("id");

											if(netrisk_hosts.getJSONObject(i).has("ip"))
												deviceIp = netrisk_hosts.getJSONObject(i).getString("ip");

											device = DeviceDAO.getDeviceByAny(deviceId, deviceIp, session);

											if (device == null) {
												System.out.println("Network.Risk: device with id = " + deviceId + " or IP = " + deviceIp +" not found; skipping");
											}
											else {

												//17/11/2020
												//At this moment, Network.Risk messages do not contain deviceId
												//This means the following (and some of the previous) code is redundant
												//Placed it here as a fail-safe in case this changes at some point
												if (device.getIp()==null || !device.getIp().equals(deviceIp)) {

													Device otherDevSameIp = DeviceDAO.getDeviceByAny(null, deviceIp, session);
													if(otherDevSameIp != null)
														DeviceDAO.updateDeviceIp(device, otherDevSameIp, deviceIp, session);
													else {
														device.setIp(deviceIp);

													}
												}

												device.setCompromiseRisk(netrisk_hosts.getJSONObject(i).getFloat("risk"));
												
												//TODO
												//See if we can get this from iIRS GET /topology/hosts
												//For now it's handled inside TrustModel.singularRiskTrust
												//if(device.getExplicitImpact() == null)
												//	device.setExplicitImpact("Normal");

												float updatedSingularRisk = TrustModel.singularRiskTrust(device.getExplicitImpact(), netrisk_hosts.getJSONObject(i).getFloat("risk"));
												device.setSingularRisk(updatedSingularRisk);

												float updatedAssociatedRisk = 
														TrustModel.associatedRiskTrust(updatedSingularRisk, netrisk_hosts.getJSONObject(i).getFloat("risk"), device.getNeighborRisk());

												DeviceDAO.updateDeviceTrust(device, device.getStatus(), device.getBehavior(), updatedAssociatedRisk, messageTopic, session);
											}

										}
									}
								}
								transact.commit();
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								transact.rollback();
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}

						break;


						//SOHO.Config of PS or iIRS REST no 5.
					case "4036.SOHO.Config":
					case "4034.SOHO.Config":
					case "4060.SOHO.Config":
					case "4063.SOHO.Config":
					case "4046.SOHO.Config":
					case "4029.SOHO.Config":
					case "207030.SOHO.Config":
					case "207029.SOHO.Config":
					case "207028.SOHO.Config":
					case "207027.SOHO.Config":
					case "SOHO.Config":
					case "5002.SOHO.Config":
					case "7246.SOHO.Config":
						try (Session session = HibernateUtil.getSession();) {
							transact = session.beginTransaction();
							try {
								JSONObject irg = new JSONObject(messageText);

								boolean msgOK = false;
								try {
									if (irg != null) {
										irg = irg.getJSONObject("payload");
										if (irg != null) {
											irg = irg.getJSONObject("irg");
											if (irg != null)
												msgOK = true;
										}
									}
								}
								catch (JSONException je) {
									; // do nothing, it will be handled below since msgOK will be false
								}

								if (! msgOK) {
									System.out.println("SOHO.Config message does not contain payload.irg field.");
								}
								else {

									JSONArray soho_hosts = null;
									try {
										soho_hosts = irg.getJSONArray("hosts");
									}
									catch (JSONException je) {
										; // do nothing, it will be handled below since soho_hosts will be null
									}
									if(soho_hosts == null) {
										System.out.println("SOHO.Config message does not contain payload.irg.hosts field.");
									}
									else {
										String id = null;
										String name = null;

										for(int i=0; i<soho_hosts.length(); i++) {

											if (soho_hosts.getJSONObject(i).has("id"))
												id = soho_hosts.getJSONObject(i).getString("id");

											if(soho_hosts.getJSONObject(i).has("name"))
												name = soho_hosts.getJSONObject(i).getString("name");

											device = DeviceDAO.getDeviceBy(id, name, null, session);
											if(device != null) {

												device.setExplicitImpact(soho_hosts.getJSONObject(i).getString("impact"));

												float updatedSingularRisk = TrustModel.singularRiskTrust(device.getExplicitImpact(), device.getCompromiseRisk());
												device.setSingularRisk(updatedSingularRisk);

												float updatedAssociatedRisk = 
														TrustModel.associatedRiskTrust(updatedSingularRisk, device.getCompromiseRisk(), device.getNeighborRisk());

												DeviceDAO.updateDeviceTrust(device, device.getStatus(), device.getBehavior(), updatedAssociatedRisk, messageTopic, session);

											}
										}
									}
								}
								transact.commit();
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								transact.rollback();
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}

						break;


						//iIRS Applicable.Mitigations
					case "4036.Applicable.Mitigations":
					case "4034.Applicable.Mitigations":
					case "4060.Applicable.Mitigations":
					case "4063.Applicable.Mitigations":
					case "4046.Applicable.Mitigations":
					case "4029.Applicable.Mitigations":
					case "207030.Applicable.Mitigations":
					case "207029.Applicable.Mitigations":
					case "207028.Applicable.Mitigations":
					case "207027.Applicable.Mitigations":
					case "Applicable.Mitigations":
					case "5002.Applicable.Mitigations":
					case "7246.Applicable.Mitigations":
						JSONObject mitigations = new JSONObject(messageText).getJSONObject("payload");

						if (mitigations.has("number"))
							System.out.println("Received iIRS Applicable.Mitigations path_no message.");

						else if(mitigations.has("remediations"))
							System.out.println("Received iIRS Applicable.Mitigations path_rem message.");

						break;


						//Profiling Service Device.Register	
					case "4036.Device.Register":
					case "4034.Device.Register":
					case "4060.Device.Register":
					case "4063.Device.Register":
					case "4046.Device.Register":
					case "4029.Device.Register":
					case "207030.Device.Register":
					case "207029.Device.Register":
					case "207028.Device.Register":
					case "207027.Device.Register":
					case "Device.Register":
					case "5002.Device.Register":
					case "7246.Device.Register":
						try (Session session = HibernateUtil.getSession();) {
							TrustedUser user;
							transact = session.beginTransaction();
							try {
								if((user = TrustedUserDAO.getTrustedUserByTrustedUserId((String) payload.get("deviceOwner"), session)) == null) {
									TrustedUserDAO.registerUser((String) payload.get("deviceOwner"), session);
									session.flush();
									user = TrustedUserDAO.getTrustedUserByTrustedUserId((String) payload.get("deviceOwner"), session);
								}

								if (((String) payload.get("deviceId")).equals(""))
									System.err.println("Received Device.Register message with empty deviceId!!! Ignoring...");
								else {
									// deviceId is valid, create or update 
									Device theDevice = DeviceDAO.getDeviceBy((String) payload.get("deviceId"), null, null, session);
									
									if (theDevice == null) {
										// not in DB, go on and create; a new device has no known vulnerabilities, nor known bad behavior; these aspects are set to 1
										theDevice = DeviceDAO.registerDevice((String) payload.get("deviceId"), user, (String) payload.get("description"), session);
										session.saveOrUpdate(theDevice);
										session.flush();
										theDevice = DeviceDAO.getDeviceBy((String) payload.get("deviceId"), null, null, session);
										DeviceDAO.updateDeviceTrust(theDevice, 1.0f, 1.0f, theDevice.getAssociatedRisk(), "initial registration", session);
										session.flush();
									}
									else {
									// device is already registered - prior registration or prior Network.Topology message.
									//	The device user and the device trust need to be updated
										theDevice.setUser(user);
										session.flush();
										DeviceDAO.updateDeviceTrust(theDevice, 1.0f, 1.0f, theDevice.getAssociatedRisk(), "update registration of registered device", session);
									}
								}
								
								transact.commit();
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								transact.rollback();
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}
						break;

						//iIRS REST no 6.
					case "4036.Device.Compromised":
					case "4034.Device.Compromised":
					case "4060.Device.Compromised":
					case "4063.Device.Compromised":
					case "4046.Device.Compromised":
					case "4029.Device.Compromised":
					case "207030.Device.Compromised":
					case "207029.Device.Compromised":
					case "207028.Device.Compromised":
					case "207027.Device.Compromised":
					case "deviceCompromised":
					case "Device.Compromised":
					case "5002.Device.Compromised":
					case "7246.Device.Compromised":
						try (Session session = HibernateUtil.getSession();) {
							transact = session.beginTransaction();
							try {
								String deviceId = null;
								String deviceIp = null;
								
								if(payload.containsKey("deviceId")) {
									deviceId = (String)(payload.get("deviceId"));
								}
								if(payload.containsKey("deviceIp")) {
									deviceIp = (String)(payload.get("deviceIp"));
								}
								
								device = DeviceDAO.getDeviceByAny(deviceId, deviceIp, session);

								if (device == null) {
									System.out.println("deviceCompromised: unknown deviceId " + deviceId + " and deviceIp " +deviceIp+"; skipping");
								}
								else {
									ArrayList<HashMap<String, String>> compromisedElements = new ArrayList<HashMap<String, String>>();
									compromisedElements = (ArrayList<HashMap<String, String>>) payload.get("compromisedElements");

									//16/11/2020 Received some Device.Compromised messages from the PS that didn't know why or how.
									//They didn't have a compromisedElements field and a NullPointerException was thrown.
									//Waiting for response with more info. Until then:
									if(compromisedElements == null) {
										System.err.println("Received Device.Compromised message with no compromisedElements field!!! Ignoring...");
										break;
									}
									if(compromisedElements.isEmpty())
										break;
									else
										device.setStatusIntegrity(false);

									JSONArray received = new JSONArray(compromisedElements);

									if(device.getCompromisedElements()==null || device.getCompromisedElements().equals("") || device.getCompromisedElements().isEmpty()) {
										device.setCompromisedElements("[]");
										DeviceDAO.updateDevice(device, session);
									}

									JSONArray current = new JSONArray(device.getCompromisedElements());

									for(int i=0; i<received.length(); i++) {
										if(!current.toString().contains(received.get(i).toString()))
											current.put((HashMap<String, Object>)received.getJSONObject(i).toMap());	
									}
									device.setCompromisedElements(current.toString());

									float statusTrustUpdate = TrustModel.statusTrust(false, device.getStatusVulns());

									DeviceDAO.updateDeviceTrust(device, statusTrustUpdate, device.getBehavior(), device.getAssociatedRisk(), messageTopic, session);
									transact.commit();
								}
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								transact.rollback();
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}
						break;


						//iIRS REST no 4.
					case "4036.Network.Topology":
					case "4034.Network.Topology":
					case "4060.Network.Topology":
					case "4063.Network.Topology":
					case "4046.Network.Topology":
					case "4029.Network.Topology":
					case "207030.Network.Topology":
					case "207029.Network.Topology":
					case "207028.Network.Topology":
					case "207027.Network.Topology":
					case "Network.Topology":
					case "5002.Network.Topology":
					case "7246.Network.Topology":
						// First process XML message
						DocumentBuilderFactory factory = null;
						DocumentBuilder builder = null;
						Document doc = null;
						NodeList machines = null;
						try  {
							factory = DocumentBuilderFactory.newInstance();
							builder = factory.newDocumentBuilder();
							doc = builder.parse(new InputSource(new StringReader(payload.get("topology").toString())));
							doc.getDocumentElement().normalize();
							machines = doc.getElementsByTagName("machine");
							for (int i=0; i<machines.getLength(); i++) {
								Element machineElement = (Element) machines.item(i);
								Element nameChild = getDirectChild(machineElement, "name");
								if (nameChild == null) {
									System.err.println("No name attribute found in machine element; machine element ignored: " + nameChild.getTextContent());
									continue;
								}
								String deviceName = nameChild.getTextContent();
								try (Session devSession = HibernateUtil.getSession();) {
									Transaction devTrans = devSession.beginTransaction();
									Device theDevice = DeviceDAO.getDeviceByAny(deviceName, null, devSession);
									if (theDevice == null) {
										TrustedUser trUser = TrustedUserDAO.getTrustedUserByTrustedUserId("TMS-unknown-user-982fa54", devSession);
										Device theDevice2 = DeviceDAO.registerDevice(deviceName, trUser, deviceName, devSession);
									}
									devTrans.commit();
								}
								catch (Exception e) {
									; // do nothing; the device was already there
								}
							}
						} catch (SAXException e) {
							System.err.println("Error processing XML topology block (malformed input?)");
							e.printStackTrace();
						} catch (IOException e) {
							System.err.println("Error processing XML topology block (malformed input?)");
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							System.err.println("Error processing XML topology block (malformed input?)");
							e.printStackTrace();
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}

						
						try (Session session = HibernateUtil.getSession();) {
							transact = session.beginTransaction();
							try {
																
								try {																
									Element machineElement;
									String deviceName;

									//primary and other IP addresses variables
									NodeList interfaces;
									Element interfacesElement;

									//vulnerabilities variables
									ArrayList<HashMap<String, String>> vulnerabilities;
									HashMap<String, String> vulnerability;

									String cvssScore;

									//directly connected devices - could be omitted - kept for code readability purposes
									NodeList directlyConnected;
									Element directlyConnectedElement;

									int counter;
									JSONObject jsonObjectTemp;
									JSONArray jsonArrayTemp;

									for (int i=0; i<machines.getLength(); i++) {

										machineElement = (Element) machines.item(i);
										//Added an id tag in the XML to map machines to devices
										Element nameChild = getDirectChild(machineElement, "name");
										if (nameChild == null) {
											System.err.println("No name attribute found in machine element; machine element ignored: " + nameChild.getTextContent());
											continue;
										}
										// get from direct child
										// deviceName = machineElement.getElementsByTagName("name").item(0).getTextContent();
										deviceName = nameChild.getTextContent();
										Element cpeElem = getDirectChild(machineElement, "cpe");
										String cpeStr = ((cpeElem != null) ? (cpeElem.getTextContent()) : (null));

										Element interfacesElem = getDirectChild(machineElement, "interfaces");
										if (interfacesElem == null) {
											System.err.println("No name interfaces found in machine element; machine element ignored: " + nameChild.getTextContent());
											continue;
										}

										Node primaryInterfaceNode = interfacesElem.getFirstChild();
										if (primaryInterfaceNode == null) {
											System.err.println("No primary interface found in machine element; machine element ignored: " + nameChild.getTextContent());
											continue;
										}
										Element primaryAddressElem = getDirectChild((Element)primaryInterfaceNode, "ipaddress");
										if (primaryAddressElem == null) {
											System.err.println("No primary interface address found in machine element; machine element ignored: " + nameChild.getTextContent());
											continue;
										}

										String primaryIpAddress =  primaryAddressElem.getTextContent();

										device = DeviceDAO.getDeviceByAny(deviceName, primaryIpAddress, session);

										if (device == null) {
											// NEVER HAPPENS, code at start ascertains that device is there. If registration is 
											// attempted here, we get an optimistic lock error
											// register the new device. At this stage, the device owner is unknown.
											// the user has been ensured to exist
											// TrustedUser user = TrustedUserDAO.getTrustedUserByTrustedUserId("TMS-unknown-user-982fa54", session);
											// device = DeviceDAO.registerDevice(deviceName, user, deviceName, session);
											System.err.println("device with name = \"" + deviceName + "\" and ip = \"" + primaryIpAddress + "\" not found; SHOULD NOT HAPPEN");
										}
										
										if ((device.getIp()==null) || !device.getIp().equals(primaryIpAddress)) {
											//primary and other ip addresses
											Device otherDevSameIp = DeviceDAO.getDeviceByAny(null, primaryIpAddress, session);
											if(otherDevSameIp != null)
												DeviceDAO.updateDeviceIp(device, otherDevSameIp, primaryIpAddress, session);
											else {
												device.setIp(primaryIpAddress);
												//Just to cover some "extreme" cases
												session.flush();
											}
										}

										//limiting <interface> tags obtained to the ones inside <interfaces> tag
										interfaces = machineElement.getElementsByTagName("interfaces");
										interfacesElement = (Element) interfaces.item(0);
										interfaces = interfacesElement.getElementsByTagName("interface");

										jsonObjectTemp = new JSONObject();

										for(int c=1; c<interfaces.getLength(); c++) {
											interfacesElement = (Element) interfaces.item(c);
											jsonObjectTemp.append("ipaddress", interfacesElement.getElementsByTagName("ipaddress").item(0).getTextContent());
										}
										device.setOtherIpAddresses(jsonObjectTemp.toString());


										//vulnerabilities
										if(machineElement.getElementsByTagName("vulnerability") != null) {

											counter = machineElement.getElementsByTagName("vulnerability").getLength();
											vulnerabilities = new ArrayList<HashMap<String, String>>();

											for(int j=0; j<counter; j++) {

												cvssScore = String.valueOf(EvdbClient.getVulnerabilityInfo(
														machineElement.getElementsByTagName("cve").item(j).getTextContent()).getCvssScore());

												vulnerability  = new HashMap<String, String>();
												vulnerability.put("type", machineElement.getElementsByTagName("type").item(j).getTextContent());
												vulnerability.put("goal", machineElement.getElementsByTagName("goal").item(j).getTextContent());
												vulnerability.put("cve", machineElement.getElementsByTagName("cve").item(j).getTextContent());
												vulnerability.put("cvssScore", cvssScore);

												vulnerabilities.add(vulnerability);

											}
											jsonArrayTemp = new JSONArray(vulnerabilities);
											device.setVulnerabilities(jsonArrayTemp.toString());


											float updatedVulnsStatusTrust = TrustModel.vulnerabilitiesStatusTrust(vulnerabilities);
											device.setStatusVulns(updatedVulnsStatusTrust);

											float updatedStatusTrust = TrustModel.statusTrust(device.getStatusIntegrity(), updatedVulnsStatusTrust);
											device.setStatus(updatedStatusTrust);

										}


										//directly connected devices
										if(machineElement.getElementsByTagName("directly-connected") != null) {


											directlyConnected = machineElement.getElementsByTagName("directly-connected");
											jsonObjectTemp = new JSONObject();

											for(int k=0; k<directlyConnected.getLength(); k++) {
												directlyConnectedElement = (Element) directlyConnected.item(k); 
												counter = directlyConnectedElement.getElementsByTagName("ipaddress").getLength();

												for(int l=0; l<counter; l++)
													jsonObjectTemp.append("ipaddresses", directlyConnectedElement.getElementsByTagName("ipaddress").item(l).getTextContent());
											}

											device.setDirectlyConnectedDevices(jsonObjectTemp.toString());

											Device neighbor;
											jsonArrayTemp = jsonObjectTemp.getJSONArray("ipaddresses");
											float neighborsSingularRisks = 0;

											//may add it in method in TrustModel class
											for(int z=0; z<jsonArrayTemp.length(); z++) {
												System.out.println("\nIP ADDRESS: " + jsonArrayTemp.getString(z) + "\n");
												neighbor = DeviceDAO.getDeviceByAny(null, jsonArrayTemp.getString(z), session);
												if (neighbor != null)
													neighborsSingularRisks += neighbor.getSingularRisk();
											}
											device.setNeighborRisk(neighborsSingularRisks);

											float updatedAssociatedRisk = 
													TrustModel.associatedRiskTrust(device.getSingularRisk(), device.getCompromiseRisk(), neighborsSingularRisks);

											DeviceDAO.updateDeviceTrust(device, device.getStatus(), device.getBehavior(), updatedAssociatedRisk, messageTopic, session);

										}

										DeviceDAO.updateDevice(device, session);
									}

								}catch (Exception e) {
									e.printStackTrace();
								}
							}
							catch (Exception e) {
								System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
								e.printStackTrace();
							}
						}
						catch (Exception e) {
							System.err.println("Message Consumer: Cannot establish connection to the database");
							e.printStackTrace();
						}

						break;

					}
				}
			}
		} catch (JMSException | JsonProcessingException | JSONException e ) {
			e.printStackTrace();
		}
		concurrencySemaphore.release();
	}
	
	public boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}

	public static Element getDirectChild(Element parent, String name)
	{
		for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if(child instanceof Element && name.equals(child.getNodeName())) return (Element) child;
		}
		return null;
	}
}
