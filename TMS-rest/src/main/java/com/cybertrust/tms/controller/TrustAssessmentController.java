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

import com.cybertrust.tms.dto.TrustAssessmentDto;
import com.cybertrust.tms.entity.TrustAssessment;
import com.cybertrust.tms.entity.TrustAssessmentId;
import com.cybertrust.tms.rest.Views;
import com.cybertrust.tms.service.TmsService;
import com.fasterxml.jackson.annotation.JsonView;

@Controller
@RequestMapping("/trustAssessment")
public class TrustAssessmentController {
	
	@Autowired
	private TmsService tmsService;
	
	@Autowired
	private ModelMapper modelMapper;
	
   
    @GetMapping("/list/all")
    @ResponseBody
    @JsonView(Views.Public.class)
    public List<TrustAssessment> getTrustAssessments(){
    	
    	return tmsService.getTrustAssessments();
    	
    }
    
    @PostMapping("/")
    @ResponseBody
    @JsonView(Views.Public.class)
    public TrustAssessmentDto addTrustAssessment(@Valid @RequestBody TrustAssessmentDto trustAssessmentDto) {
    	
    	TrustAssessment trustAssessment = modelMapper.map(trustAssessmentDto, TrustAssessment.class);
    	
    	tmsService.saveTrustAssessment(trustAssessment);
    	
    	return trustAssessmentDto;
    	
    }
       
    @DeleteMapping("/")
    @ResponseBody
    @JsonView(Views.Internal.class)
    public String deleteTrustAssessment(@Valid @RequestBody TrustAssessmentId trustAssessmentId) {
    	
    	tmsService.deleteTrustAssessment(trustAssessmentId);
    	
    	return "Deleted trustAssessment with id - " + trustAssessmentId;
    	
    }

}
