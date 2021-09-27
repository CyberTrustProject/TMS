package com.cybertrust.tms.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.cybertrust.tms.entity.TrustedUser;
import com.cybertrust.tms.rest.Views;
import com.cybertrust.tms.service.TmsService;
import com.fasterxml.jackson.annotation.JsonView;

@Controller
@RequestMapping("/trustedUser")
public class TrustedUserController {
	
	@Autowired
	private TmsService tmsService;
	
    @GetMapping("/{trustedUserId}")
    @ResponseBody
    @JsonView(Views.Public.class)
	public TrustedUser getTrustedUser(@PathVariable("trustedUserId") String trustedUserId) {
    	
    	TrustedUser user = tmsService.getTrustedUserByTrustedUserId(trustedUserId);
    	
    	if(user == null)
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "trusted user " + trustedUserId + " not found");
    		
    	return user;
    	
    }
    
    @GetMapping("/list/all")
    @ResponseBody
    @JsonView(Views.Public.class)
    public List<TrustedUser> getAllTrustedUsers(){
    	
    	return tmsService.getTrustedUsers();
    	
    }
    
    @GetMapping("/list/{trustedUserIds}")
    @ResponseBody
    @JsonView(Views.Public.class)
    public List<TrustedUser> getTrustedUsers(@PathVariable("trustedUserIds") String trustedUserIds){
    	
    	List<String> ids =  Arrays.asList(trustedUserIds.split(","));
    	
    	if(ids != null) {
    		
    		for(String id: ids) {
    			if(tmsService.getTrustedUserByTrustedUserId(id) == null)
    				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "trusted user " + id + " not found");
    		}
    		
    		List<TrustedUser> result = new ArrayList<TrustedUser>();
    		
    		for(String id_str : ids)
    			result.add(tmsService.getTrustedUserByTrustedUserId(id_str));
    	
    		return result;
    	}
    	else
    		return null;
    	
    }
    
    @PutMapping("/")
    @JsonView(Views.Public.class)
    @ResponseBody
    public TrustedUser updateTrustedUser(@Valid @RequestBody TrustedUser trustedUser) {
    	TrustedUser user;
    	if((user = tmsService.getTrustedUserByTrustedUserId(trustedUser.getTrustedUserId())) != null)
    		trustedUser.setId(user.getId());
    	tmsService.saveTrustedUser(trustedUser);
    	
    	return trustedUser;
    	
    }
    
    @DeleteMapping("/{trustedUserId}")
    @JsonView(Views.Public.class)
    @ResponseBody
    public void deleteTrustedUser(@PathVariable String trustedUserId) {
    	
    	if(tmsService.getTrustedUserByTrustedUserId(trustedUserId) == null)
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "trusted user " + trustedUserId + " not found");
    	
    	tmsService.deleteTrustedUser(trustedUserId);
    	
    }
    

}
