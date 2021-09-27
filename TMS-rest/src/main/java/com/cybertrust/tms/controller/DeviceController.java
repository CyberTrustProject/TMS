package com.cybertrust.tms.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import com.cybertrust.tms.cryptoutils.ConfigManagement;
import com.cybertrust.tms.dao.TrustedUserDAO;
import com.cybertrust.tms.dto.DeviceDto;
import com.cybertrust.tms.dto.DeviceMapper;
import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.DeviceHistoricalTrustData;
import com.cybertrust.tms.logging.TMSLogger;
import com.cybertrust.tms.rest.Views;
import com.cybertrust.tms.service.TmsService;
import com.fasterxml.jackson.annotation.JsonView;

@Controller
@EnableTransactionManagement
@Transactional 
@RequestMapping("/device")
public class DeviceController {
	
	private final DeviceMapper deviceMapper = DeviceMapper.INSTANCE;
	
	@Autowired
	private TmsService tmsService;
	
	@Autowired
	private TrustedUserDAO trustedUserDAO;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ConfigManagement configManagement;
	
	@GetMapping("/{deviceId}")
    @ResponseBody
	public DeviceDto getDevice(@PathVariable("deviceId") String deviceId, HttpServletRequest request) {
    	
		Device device = tmsService.getDeviceByDeviceId(deviceId);
		DeviceDto result;
		
    	if (device != null) {
    		result = modelMapper.map(device, DeviceDto.class);
    		TMSLogger.getLoggger().info(String.format("%1$s: %2$s", (String) request.getAttribute(
    		        HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE), "OK")); 
    		return result;
    	}
    	else {
    		TMSLogger.getLoggger().info(String.format("%1$s: %2$s", (String) request.getAttribute(
    		        HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE), "Not found"));    		
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "device " + deviceId + " not found");
    	}
	}
	
    
    @GetMapping("/")
    @ResponseBody
    @JsonView(Views.Public.class)
    public List<Device> getDevices(){
    	
    	return tmsService.getDevices();
    	
    }
    

    @PostMapping("/addKnown")
    @ResponseBody
    public DeviceDto addDevice(@Valid @RequestBody DeviceDto deviceDto) {
    	
    	Device device = modelMapper.map(deviceDto, Device.class);
    	
    	device.setLastTrustTimestamp(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
		        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()));
    	
    	tmsService.saveDeviceWithUser(deviceDto.getUserId(), device);
    	
    	DeviceHistoricalTrustData newEntry = tmsService.getDeviceByDeviceId(deviceDto.getDeviceId()).createHistoricalEntry();
    	tmsService.saveDeviceHistoricalTrustData(newEntry);
    	
    	try {
			configManagement.addTrustedEntity(device.getName(), device.getPublicKey());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
    	return deviceDto;
    	
    }
    
    @DeleteMapping("/{deviceId}")
    @ResponseBody
    @JsonView(Views.Public.class)
    public String deleteDevice(@PathVariable int deviceId) {
    	
    	tmsService.deleteDevice(deviceId);   	
    	return "Deleted device with id - " + deviceId;  	
    }

    
    // Not sure we need the following 
    @PostMapping("/{userId}")
    @ResponseBody
    @JsonView(Views.Public.class)
    public Device addDeviceWithUser(@PathVariable("userId") int userId, @Valid @RequestBody Device device) {
    	
    	//because we are using the saveOrUpdate method in the DAO
    	device.setId(0);
    	
    	return tmsService.saveDeviceWithUser(userId, device);
    	
    }
    
}
