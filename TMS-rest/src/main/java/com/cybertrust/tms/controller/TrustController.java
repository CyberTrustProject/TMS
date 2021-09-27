package com.cybertrust.tms.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.cybertrust.tms.dto.TrustDimensionScore;
import com.cybertrust.tms.dto.TrustScoreElementDto;
import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.DeviceHistoricalTrustData;
import com.cybertrust.tms.service.TmsService;

@Controller
@RequestMapping("/trust")
public class TrustController {

	@Autowired
	private TmsService tmsService;

	@GetMapping("/info/{deviceId}")
	@ResponseBody
	public ArrayList<TrustScoreElementDto> getDeviceTrust (@Valid @PathVariable("deviceId") String deviceId, 
			@Valid @RequestParam(required = false) List<String> trustDimensionsSpec, 
			@Valid @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since, 
			@Valid @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime until) {

		ArrayList<TrustScoreElementDto> result = new ArrayList<TrustScoreElementDto>();
		ArrayList<TrustDimensionScore> scores = new ArrayList<TrustDimensionScore>();
		ArrayList<TrustDimensionScore> computedScores = new ArrayList<TrustDimensionScore>();

		Device device = tmsService.getDeviceByDeviceId(deviceId);

		if(device == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "device " + deviceId + " not found");

		if(since==null && until==null) {

			if(trustDimensionsSpec==null) {

				if(device.getExplicitLevel() == null) {						
					scores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
					scores.add(new TrustDimensionScore("status", device.getStatus()));
					scores.add(new TrustDimensionScore("behavior", device.getBehavior()));
					scores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

					result.add(new TrustScoreElementDto
							(deviceId, "", "automatic", scores, null, device.getLastTrustTimestamp(), ""));
				}
				else {
					scores.add(new TrustDimensionScore("overall", Float.parseFloat(device.getExplicitLevel())));
					scores.add(new TrustDimensionScore("status", Float.parseFloat(device.getExplicitLevel())));
					scores.add(new TrustDimensionScore("behavior", Float.parseFloat(device.getExplicitLevel())));
					scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(device.getExplicitLevel())));

					computedScores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
					computedScores.add(new TrustDimensionScore("status", device.getStatus()));
					computedScores.add(new TrustDimensionScore("behavior", device.getBehavior()));
					computedScores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

					result.add(new TrustScoreElementDto
							(deviceId, "", "explicit", scores, computedScores, device.getLastTrustTimestamp(), ""));
				}

			}
			else {

				if(device.getExplicitLevel() == null) {
					if(trustDimensionsSpec.contains("overall"))
						scores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
					if(trustDimensionsSpec.contains("status"))
						scores.add(new TrustDimensionScore("status", device.getStatus()));
					if(trustDimensionsSpec.contains("behavior"))
						scores.add(new TrustDimensionScore("behavior", device.getBehavior()));
					if(trustDimensionsSpec.contains("associatedRisk"))
						scores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

					result.add(new TrustScoreElementDto
							(deviceId, "", "automatic", scores, null, device.getLastTrustTimestamp(), ""));
				}

				else {
					if(trustDimensionsSpec.contains("overall")) {
						scores.add(new TrustDimensionScore("overall", Float.parseFloat(device.getExplicitLevel())));
						computedScores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
					}

					if(trustDimensionsSpec.contains("status")) {
						scores.add(new TrustDimensionScore("status", Float.parseFloat(device.getExplicitLevel())));
						computedScores.add(new TrustDimensionScore("status", device.getStatus()));
					}

					if(trustDimensionsSpec.contains("behavior")) {
						scores.add(new TrustDimensionScore("behavior", Float.parseFloat(device.getExplicitLevel())));
						computedScores.add(new TrustDimensionScore("behavior", device.getBehavior()));
					}
					if(trustDimensionsSpec.contains("associatedRisk")) {
						scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(device.getExplicitLevel())));
						computedScores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));
					}

					result.add(new TrustScoreElementDto
							(deviceId, "", "explicit", scores, computedScores, device.getLastTrustTimestamp(), ""));
				}

			}

		}
		else if(since!=null) {

			if(until == null)
				until = LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
				        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter());

			if(since.compareTo(until) > 0)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It must be since <= until.");

			ArrayList<DeviceHistoricalTrustData> deviceHistoricalSinceUntil = new ArrayList<DeviceHistoricalTrustData>(); 

			for(DeviceHistoricalTrustData ta : device.getDeviceHistoricalTrustData()) {
				if(ta.getTstamp().compareTo(since) >= 0 && ta.getTstamp().compareTo(until) <= 0)
					deviceHistoricalSinceUntil.add(ta);
			}

			if(trustDimensionsSpec==null) {
				for(DeviceHistoricalTrustData ta : deviceHistoricalSinceUntil) {

					if(ta.getExplicitLevel() == null) {
						scores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						scores.add(new TrustDimensionScore("status", ta.getStatus()));
						scores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						scores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));

						result.add(new TrustScoreElementDto
								(deviceId, "", "automatic", scores, null, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
					}

					else {
						scores.add(new TrustDimensionScore("overall", Float.parseFloat(ta.getExplicitLevel())));
						scores.add(new TrustDimensionScore("status", Float.parseFloat(ta.getExplicitLevel())));
						scores.add(new TrustDimensionScore("behavior", Float.parseFloat(ta.getExplicitLevel())));
						scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(ta.getExplicitLevel())));

						computedScores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						computedScores.add(new TrustDimensionScore("status", ta.getStatus()));
						computedScores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						computedScores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));

						result.add(new TrustScoreElementDto
								(deviceId, "", "explicit", scores, computedScores, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
						computedScores = new ArrayList<TrustDimensionScore>();
					}

				}

			}
			else {
				for(DeviceHistoricalTrustData ta : deviceHistoricalSinceUntil) {

					if(ta.getExplicitLevel() == null) {
						if(trustDimensionsSpec.contains("overall"))
							scores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						if(trustDimensionsSpec.contains("status"))
							scores.add(new TrustDimensionScore("status", ta.getStatus()));
						if(trustDimensionsSpec.contains("behavior"))
							scores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						if(trustDimensionsSpec.contains("associatedRisk"))
							scores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));


						result.add(new TrustScoreElementDto
								(deviceId, "", "automatic", scores, null, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
					}

					else {
						if(trustDimensionsSpec.contains("overall")) {
							scores.add(new TrustDimensionScore("overall", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						}

						if(trustDimensionsSpec.contains("status")) {
							scores.add(new TrustDimensionScore("status", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("status", ta.getStatus()));
						}

						if(trustDimensionsSpec.contains("behavior")) {
							scores.add(new TrustDimensionScore("behavior", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						}
						if(trustDimensionsSpec.contains("associatedRisk")) {
							scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));
						}

						result.add(new TrustScoreElementDto
								(deviceId, "", "explicit", scores, computedScores, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
						computedScores = new ArrayList<TrustDimensionScore>();
					}

				}

			}

		}
		else if(since==null && until!=null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The parameter until was defined, but parameter since was not.");

		return result;

	}

	@GetMapping("")
	@ResponseBody
	public ArrayList<TrustScoreElementDto> getDevicesTrust (@Valid @RequestParam List<String> deviceId, 
			@Valid @RequestParam(required = false) List<String> trustDimensionsSpec, 
			@Valid @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since, 
			@Valid @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime until){

		ArrayList<TrustScoreElementDto> result = new ArrayList<TrustScoreElementDto>();
		ArrayList<TrustDimensionScore> scores = new ArrayList<TrustDimensionScore>();
		ArrayList<TrustDimensionScore> computedScores = new ArrayList<TrustDimensionScore>();

		Device device;

		for(String id : deviceId) {
			if(tmsService.getDeviceByDeviceId(id) == null)
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "device " + id + " not found");
		}

		if(since==null && until==null) {

			if(trustDimensionsSpec==null) {

				for(String id : deviceId) {
					device = tmsService.getDeviceByDeviceId(id);

					if(device.getExplicitLevel() == null) {						
						scores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
						scores.add(new TrustDimensionScore("status", device.getStatus()));
						scores.add(new TrustDimensionScore("behavior", device.getBehavior()));
						scores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

						result.add(new TrustScoreElementDto
								(id, "", "automatic", scores, null, device.getLastTrustTimestamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
					}
					else {
						scores.add(new TrustDimensionScore("overall", Float.parseFloat(device.getExplicitLevel())));
						scores.add(new TrustDimensionScore("status", Float.parseFloat(device.getExplicitLevel())));
						scores.add(new TrustDimensionScore("behavior", Float.parseFloat(device.getExplicitLevel())));
						scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(device.getExplicitLevel())));

						computedScores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
						computedScores.add(new TrustDimensionScore("status", device.getStatus()));
						computedScores.add(new TrustDimensionScore("behavior", device.getBehavior()));
						computedScores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

						result.add(new TrustScoreElementDto
								(id, "", "explicit", scores, computedScores, device.getLastTrustTimestamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
						computedScores = new ArrayList<TrustDimensionScore>();
					}

				}

			}
			else {

				for(String id : deviceId) {
					device = tmsService.getDeviceByDeviceId(id);

					if(device.getExplicitLevel() == null) {
						if(trustDimensionsSpec.contains("overall"))
							scores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
						if(trustDimensionsSpec.contains("status"))
							scores.add(new TrustDimensionScore("status", device.getStatus()));
						if(trustDimensionsSpec.contains("behavior"))
							scores.add(new TrustDimensionScore("behavior", device.getBehavior()));
						if(trustDimensionsSpec.contains("associatedRisk"))
							scores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

						result.add(new TrustScoreElementDto
								(id, "", "automatic", scores, null, device.getLastTrustTimestamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
					}

					else {
						if(trustDimensionsSpec.contains("overall")) {
							scores.add(new TrustDimensionScore("overall", Float.parseFloat(device.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
						}

						if(trustDimensionsSpec.contains("status")) {
							scores.add(new TrustDimensionScore("status", Float.parseFloat(device.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("status", device.getStatus()));
						}

						if(trustDimensionsSpec.contains("behavior")) {
							scores.add(new TrustDimensionScore("behavior", Float.parseFloat(device.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("behavior", device.getBehavior()));
						}
						if(trustDimensionsSpec.contains("associatedRisk")) {
							scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(device.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));
						}

						result.add(new TrustScoreElementDto
								(id, "", "explicit", scores, computedScores, device.getLastTrustTimestamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
						computedScores = new ArrayList<TrustDimensionScore>();
					}

				}

			}

		}
		else if(since!=null) {

			if(until == null)
				until = LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
				        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter());

			if(since.compareTo(until) > 0)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It must be since <= until.");

			ArrayList<DeviceHistoricalTrustData> deviceHistoricalSinceUntil = new ArrayList<DeviceHistoricalTrustData>();

			for(String id : deviceId) {
				device = tmsService.getDeviceByDeviceId(id);
				for(DeviceHistoricalTrustData ta : device.getDeviceHistoricalTrustData()) {
					if(ta.getTstamp().compareTo(since) >= 0 && ta.getTstamp().compareTo(until) <= 0)
						deviceHistoricalSinceUntil.add(ta);
				}
			}

			if(trustDimensionsSpec==null) {

				for(DeviceHistoricalTrustData ta : deviceHistoricalSinceUntil) {

					if(ta.getExplicitLevel() == null) {
						scores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						scores.add(new TrustDimensionScore("status", ta.getStatus()));
						scores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						scores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));
						//if we want uuid in historical, change the first argument
						result.add(new TrustScoreElementDto
								(tmsService.getDevice(ta.getDeviceId()).getDeviceId(), "", "automatic", scores, null, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
					}

					else {
						scores.add(new TrustDimensionScore("overall", Float.parseFloat(ta.getExplicitLevel())));
						scores.add(new TrustDimensionScore("status", Float.parseFloat(ta.getExplicitLevel())));
						scores.add(new TrustDimensionScore("behavior", Float.parseFloat(ta.getExplicitLevel())));
						scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(ta.getExplicitLevel())));

						computedScores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						computedScores.add(new TrustDimensionScore("status", ta.getStatus()));
						computedScores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						computedScores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));
						//if we want uuid in historical, change the first argument
						result.add(new TrustScoreElementDto
								(tmsService.getDevice(ta.getDeviceId()).getDeviceId(), "", "explicit", scores, computedScores, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
						computedScores = new ArrayList<TrustDimensionScore>();
					}

				}

			}
			else {
				for(DeviceHistoricalTrustData ta : deviceHistoricalSinceUntil) {

					if(ta.getExplicitLevel() == null) {
						if(trustDimensionsSpec.contains("overall"))
							scores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						if(trustDimensionsSpec.contains("status"))
							scores.add(new TrustDimensionScore("status", ta.getStatus()));
						if(trustDimensionsSpec.contains("behavior"))
							scores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						if(trustDimensionsSpec.contains("associatedRisk"))
							scores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));

						//if we want uuid in historical, change the first argument
						result.add(new TrustScoreElementDto
								(tmsService.getDevice(ta.getDeviceId()).getDeviceId(), "", "automatic", scores, null, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
					}

					else {
						if(trustDimensionsSpec.contains("overall")) {
							scores.add(new TrustDimensionScore("overall", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("overall", ta.getTrustLevel()));
						}

						if(trustDimensionsSpec.contains("status")) {
							scores.add(new TrustDimensionScore("status", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("status", ta.getStatus()));
						}

						if(trustDimensionsSpec.contains("behavior")) {
							scores.add(new TrustDimensionScore("behavior", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("behavior", ta.getBehavior()));
						}
						if(trustDimensionsSpec.contains("associatedRisk")) {
							scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(ta.getExplicitLevel())));
							computedScores.add(new TrustDimensionScore("associatedRisk", ta.getAssociatedRisk()));
						}
						//if we want uuid in historical, change the first argument
						result.add(new TrustScoreElementDto
								(tmsService.getDevice(ta.getDeviceId()).getDeviceId(), "", "explicit", scores, computedScores, ta.getTstamp(), ""));

						scores = new ArrayList<TrustDimensionScore>();
						computedScores = new ArrayList<TrustDimensionScore>();
					}

				}

			}

		}
		else if(since==null && until!=null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The parameter until was defined, but parameter since was not.");


		return result;

	}
	
	@PutMapping("/explicitLevel/{deviceId}")
	@ResponseBody
	public TrustScoreElementDto setExplicitLevel(@Valid @PathVariable("deviceId") String deviceId, @Valid @RequestParam String trustScore) {

		Device device = tmsService.getDeviceByDeviceId(deviceId);

		if(device == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "device " + deviceId + " not found");

		device.setExplicitLevel(trustScore);
		device.setLastTrustTimestamp(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
		        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()));
		tmsService.saveDevice(device);

		DeviceHistoricalTrustData newEntry = device.createHistoricalEntry();
    	tmsService.saveDeviceHistoricalTrustData(newEntry);

		ArrayList<TrustDimensionScore> scores = new ArrayList<TrustDimensionScore>();
		ArrayList<TrustDimensionScore> computedScores = new ArrayList<TrustDimensionScore>();

		scores.add(new TrustDimensionScore("overall", Float.parseFloat(device.getExplicitLevel())));
		scores.add(new TrustDimensionScore("status", Float.parseFloat(device.getExplicitLevel())));
		scores.add(new TrustDimensionScore("behavior", Float.parseFloat(device.getExplicitLevel())));
		scores.add(new TrustDimensionScore("associatedRisk", Float.parseFloat(device.getExplicitLevel())));

		computedScores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
		computedScores.add(new TrustDimensionScore("status", device.getStatus()));
		computedScores.add(new TrustDimensionScore("behavior", device.getBehavior()));
		computedScores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

		return new TrustScoreElementDto
				(deviceId, "", "explicit", scores, computedScores, device.getLastTrustTimestamp(), "");

	}

	@DeleteMapping("/explicitLevel/{deviceId}")
	@ResponseBody
	public TrustScoreElementDto deleteExplicitLevel(@Valid @PathVariable("deviceId") String deviceId) {

		Device device = tmsService.getDeviceByDeviceId(deviceId);

		if(device == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "device " + deviceId + " not found");

		device.setExplicitLevel(null);
		device.setLastTrustTimestamp(LocalDateTime.parse(Instant.now().truncatedTo(ChronoUnit.MICROS).toString().replace("T", " ").replace("Z",  ""), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
		        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).toFormatter()));
		tmsService.saveDevice(device);

		DeviceHistoricalTrustData newEntry = device.createHistoricalEntry();
    	tmsService.saveDeviceHistoricalTrustData(newEntry);

		ArrayList<TrustDimensionScore> scores = new ArrayList<TrustDimensionScore>();

		scores.add(new TrustDimensionScore("overall", device.getTrustLevel()));
		scores.add(new TrustDimensionScore("status", device.getStatus()));
		scores.add(new TrustDimensionScore("behavior", device.getBehavior()));
		scores.add(new TrustDimensionScore("associatedRisk", device.getAssociatedRisk()));

		return new TrustScoreElementDto
				(deviceId, "", "automatic", scores, null, device.getLastTrustTimestamp(), "");

	}

}
