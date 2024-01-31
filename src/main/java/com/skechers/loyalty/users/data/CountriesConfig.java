package com.skechers.loyalty.users.data;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * This class holds configuration (key,value) pare for respective countries.
 * 
 * @author pkumar
 *
 */

@Slf4j
public class CountriesConfig {

	private Map<String, Map<String, String>> config = new HashMap<>();
	

	public CountriesConfig() {
		super();
	}

	public String getParameter(String parameterName) {
		String countryCode = MDC.get("countryCode");
		Map<String, String> countryConfig = config.get(countryCode);

		if (countryConfig != null) {
			return countryConfig.get(parameterName);
		}
		return null;
	}

	public void setParameter(String countryCode, String parameterName, String parameterValue) {
		Map<String, String> countryConfig = config.get(countryCode);
		log.debug("Set param {}:{}", countryCode, parameterName);
		if (countryConfig == null) {
			countryConfig = new HashMap<>();
		}
		countryConfig.put(parameterName, parameterValue);
		config.put(countryCode, countryConfig);
	}

	public boolean isCountryNotAvailable(String countryCode) {
		Map<String, String> countryConfig = config.get(countryCode);
		if (CollectionUtils.isEmpty(countryConfig)) {
			return true;
		}
		return false;
	}

}
