package com.skechers.loyalty.users.controllers;

import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/application-status")
public class ApplicationStatusController {
	
	@Value("${spring.profiles.active}")
	private String env;
	
	private String version;
	
	@Value("${loyalty.region}")
	private String region;
	
	
	
	public ApplicationStatusController() {
		Properties prop;
		try {
			prop = PropertiesLoaderUtils.loadAllProperties("version.properties");
			version = prop.getProperty("version");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("unchecked")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public String appStatus(){
		JSONObject status = new JSONObject();
		status.put("environment", env);
		status.put("version", version);
		status.put("region", region);
		return status.toJSONString();		
	}

}
