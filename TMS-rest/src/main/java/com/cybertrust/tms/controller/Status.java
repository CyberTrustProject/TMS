package com.cybertrust.tms.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.cybertrust.tms.dto.StatusDto;


@Controller
@EnableTransactionManagement
@Transactional 
@RequestMapping("/")
public class Status {
	
	@GetMapping("/test")
    @ResponseBody
	public StatusDto getStatus() {
    	
		StatusDto result = new StatusDto().setServiceOK().setDbOK();
     	return result;
    }
}