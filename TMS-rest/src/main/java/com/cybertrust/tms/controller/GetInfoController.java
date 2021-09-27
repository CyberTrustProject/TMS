package com.cybertrust.tms.controller;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cybertrust.tms.cryptoutils.ConfigManagement;

@Controller
@RequestMapping("/getinfo")
public class GetInfoController {
	
	@Autowired
	private ConfigManagement configManagement;
	
	@GetMapping(value="", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getTmsInfo() {
		
		String info = null;
		try {
			info = String.format("{\"name\":%s,\n\"publicKey\":%s\n}", configManagement.getMyName(), configManagement.getStringCertificate());
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
		
	}

}
