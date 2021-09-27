package com.cybertrust.tms.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cybertrust.tms.entity.TrustedUser;
import com.cybertrust.tms.service.TmsService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/rest-api")
public class PeerTmsRestController {
	
	@Autowired
	private TmsService tmsService;
	
	@GetMapping("/trust/info/{trustedUserId}")
	@JsonView(Views.Public.class)
	public TrustedUser getTrustedUser(@PathVariable int trustedUserId) {
		
		return tmsService.getTrustedUser(trustedUserId);
		
	}

}
