package com.cybertrust.tmslistener.dao;

import org.hibernate.Session;

import com.cybertrust.tmslistener.entity.PeerTms;

public class PeerTmsDAO {
	
	public static PeerTms getPeerTms(int id, Session session) {
		return session.get(PeerTms.class, id);
	}

}
