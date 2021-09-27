package com.cybertrust.tms.dao;

import java.util.List;

import com.cybertrust.tms.entity.PeerTms;

public interface PeerTmsDAO {
	
	public PeerTms getPeerTms(int id);
	
	public PeerTms getPeerTmsByPeerTmsId(String peerTmsId);
	
	public List<PeerTms> getPeerTmsSome(List<String> theIds);
	
	public List<PeerTms> getPeerTmsAll();
	
	public void savePeerTms(PeerTms peerTms);
	
	public void updatePeerTms(PeerTms peerTms);
	
	public void deletePeerTms(String peerTmsId);

}
