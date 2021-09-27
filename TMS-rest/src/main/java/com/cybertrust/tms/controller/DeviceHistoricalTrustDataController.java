package com.cybertrust.tms.controller;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cybertrust.tms.dto.DeviceHistoricalTrustDataDto;
import com.cybertrust.tms.entity.DeviceHistoricalTrustData;
import com.cybertrust.tms.entity.DeviceHistoricalTrustDataId;
import com.cybertrust.tms.rest.Views;
import com.cybertrust.tms.service.TmsService;
import com.fasterxml.jackson.annotation.JsonView;

@Controller
@RequestMapping("/deviceHistorical")
public class DeviceHistoricalTrustDataController {
	
	@Autowired
	private TmsService tmsService;
	
	@Autowired
	private ModelMapper modelMapper;
	
    
    @PostMapping("/")
    @ResponseBody
    public DeviceHistoricalTrustDataDto addDeviceHistoricalTrustData(@Valid @RequestBody DeviceHistoricalTrustDataDto deviceHistoricalDto) {
    	
    	DeviceHistoricalTrustData deviceHistorical = modelMapper.map(deviceHistoricalDto, DeviceHistoricalTrustData.class);
    	
    	tmsService.saveDeviceHistoricalTrustData(deviceHistorical);
    	
    	return deviceHistoricalDto;
    	
    }
}
