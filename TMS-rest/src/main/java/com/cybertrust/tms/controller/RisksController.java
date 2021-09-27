package com.cybertrust.tms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.cybertrust.tms.dto.RiskDto;
import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.service.TmsService;

@Controller
@RequestMapping("/risks")
public class RisksController {
	
	@Autowired
	private TmsService tmsService;
	
	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/prioritize")
	@ResponseBody
	@Validated
	public List<RiskDto> getPrioritizedRisks(@Valid @RequestParam(defaultValue="10") Integer numRisks) {
		if(numRisks <= 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The number of risks must be a positive integer");
		
		List<Device> devices = new ArrayList<Device>();
		List<RiskDto> riskDtos = new ArrayList<RiskDto>();
		
		devices = tmsService.getRisksPrioritized(numRisks);
		
		if(devices == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No devices found");
		
		for(Device d : devices)
			riskDtos.add(modelMapper.map(d, RiskDto.class));
		
		return riskDtos;
		
	}

}
