package com.cybertrust.tms.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.cybertrust.tms.cryptoutils.ConfigManagement;
import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.PeerTms;
import com.cybertrust.tms.rest.Views;
import com.cybertrust.tms.service.TmsService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping("/peertms")
public class PeerTmsController {
	
	@Autowired
	private TmsService tmsService;
	
	@Autowired
	private ConfigManagement configManagement;
	
	@Autowired
	private ModelMapper modelMapper;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("")
	@ResponseBody
	@JsonView(Views.Public.class)
	public List<PeerTms> getSpecifiedPeerTms(@RequestParam() String peerTMSids, HttpServletRequest request, HttpServletResponse response) {
		
		List<String> idList = Arrays.asList(peerTMSids.split(","));
		if (idList.size() == 0) {
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST); 
			return new ArrayList<PeerTms>();
		}

    	return tmsService.getPeerTms(idList);	
    }	
	
	@GetMapping("/{peerTmsId}")
    @ResponseBody
    @JsonView(Views.Public.class)
	public PeerTms getPeerTms(@PathVariable("peerTmsId") String peerTmsId) {
		
		PeerTms peerTms = tmsService.getPeerTmsByPeerTmsId(peerTmsId);
		
		if(peerTms == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PeerTMS " + peerTmsId + " not found");
		
		return peerTms;
    }
    
    @GetMapping("/list/all")
    @ResponseBody
    @JsonView(Views.Public.class)
    public List<PeerTms> getPeerTmsAll(){
    	
    	return tmsService.getPeerTmsAll();
    	
    }
    
    @PutMapping("/{peerTmsId}")
    @ResponseBody
    public JsonNode updatePeerTms(@PathVariable("peerTmsId") String peerTmsId, @Valid @RequestBody JsonNode requestBody) {
    	
    	if(!peerTmsId.equals(requestBody.get("peerTmsId").asText()))
    		throw new ResponseStatusException(HttpStatus.CONFLICT, "PeerTms cannot be updated (path PeerTms id = " + peerTmsId + ", "
    				+ "body PeerTms id = " + requestBody.get("peerTmsId") + ")");
    	
    	if(requestBody.get("description") == null || requestBody.get("publicKey") == null || requestBody.get("name") == null || requestBody.get("deviceId") == null) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The fields description, publicKey, name and deviceId cannot be null.");
    	}
    	
    	PeerTms peerTms = tmsService.getPeerTmsByPeerTmsId(peerTmsId);
    	Device device = null;
    	
    	if(peerTms == null) {
    		
    		if((device = tmsService.getDeviceByDeviceId(requestBody.get("deviceId").asText())) == null)
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The device " + requestBody.get("deviceId").asText() + " doesn't exist.");
    		
    		peerTms = new PeerTms();
    		peerTms.setPeerTmsId(peerTmsId);
    		peerTms.setName(requestBody.get("name").asText());
    		peerTms.setDevice(device);
    	}
    	
    	peerTms.setDescription(requestBody.get("description").asText());
    	peerTms.setPublicKey(requestBody.get("publicKey").asText());
    	
    	//TODO
    	//Maybe it's not the best idea to handle the exceptions of ConfigManagement methods in the controller.
    	try {
			tmsService.updatePeerTms(peerTms);
			if(device == null)
				configManagement.updateTrustedEntity(peerTms.getName(), peerTms.getPublicKey());
			else
				configManagement.addTrustedEntity(peerTms.getName(), peerTms.getPublicKey());
		} catch (IOException e) {
			System.out.println("Couldn't create/update the public certificate of the Peer TMS.");
		} catch (GeneralSecurityException e) {
			System.out.println("Couldn't create/update the public certificate of the Peer TMS.");
		}
    	
    	JsonNode responseBody;
		try {
			String str = mapper.writerWithView(Views.Public.class).writeValueAsString(peerTms);
			responseBody = mapper.readTree(str);
			((ObjectNode) responseBody).put("deviceId", requestBody.get("deviceId").asText());
			return responseBody;
		} catch (JsonMappingException e) {
			System.out.println("Couldn't create ResponseBody.");
		} catch (JsonProcessingException e) {
			System.out.println("Couldn't create ResponseBody.");
		}
		
		return null;
    	
    }
       
    @DeleteMapping("/{peerTmsId}")
    @ResponseBody
    @JsonView(Views.Public.class)
    public void deleteDevice(@PathVariable String peerTmsId) {
    	
    	if(tmsService.getPeerTmsByPeerTmsId(peerTmsId) == null)
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PeerTMS " + peerTmsId + " not found");
    	
    	tmsService.deletePeerTms(peerTmsId);
    	
    }

}
