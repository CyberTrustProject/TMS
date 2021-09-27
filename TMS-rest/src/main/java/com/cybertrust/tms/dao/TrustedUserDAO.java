package com.cybertrust.tms.dao;

import java.util.List;

import com.cybertrust.tms.entity.TrustedUser;

public interface TrustedUserDAO {
	
	public TrustedUser getTrustedUser(int id);
	
	public TrustedUser getTrustedUserByTrustedUserId(String trustedUserId);
	
	public List<TrustedUser> getTrustedUsers();
	
	public void saveTrustedUser(TrustedUser trustedUser);
	
	public void deleteTrustedUser(String id);

}
