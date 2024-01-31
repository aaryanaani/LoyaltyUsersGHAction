package com.skechers.loyalty.users.utils;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.skechers.loyalty.users.data.CountriesConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ParameterUtil {

	private static final String TOTAL_TIME = "Total time {}";

	@Autowired
	private AWSSimpleSystemsManagement ssmClient;

	@Autowired
	private Environment environment;

	@Autowired
	private CountriesConfig countriesConfig;

	public static String COUNTRY_PARAM_PREFIX = "__";

	public String getParameterValue(String paramName) {
		long startTime = System.currentTimeMillis();

		String value = getCountryConfigParameter(paramName);
		if (null == value) {
			value = getRegionConfigParameter(paramName);

			if (value == null) {
				value = getProfileConfigParameter(paramName);
			}
		}
		log.info(TOTAL_TIME, (System.currentTimeMillis() - startTime));

		return value;

	}

	public String getCountryConfigPath() {
		String path = getRegionConfigPath() + MDC.get("countryCode") + "/" + COUNTRY_PARAM_PREFIX;

		return path;
	}

	public String getRegionConfigPath() {
		String path = getProfileConfigPath() + environment.getProperty("loyalty.region") + "/";

		return path;
	}

	public String getProfileConfigPath() {
		String path = environment.getProperty("aws.paramstore.prefix") + "/"
				+ environment.getProperty("aws.paramstore.name")
				+ environment.getProperty("aws.paramstore.profileSeparator")
				+ environment.getProperty("spring.profiles.active") + "/";

		return path;
	}

	public String getCountryConfigParameter(String paramName) {
		log.info("Looking into country config: " + paramName);
		try {
			long startTime = System.currentTimeMillis();
			GetParameterRequest getParameterRequest = new GetParameterRequest();
			log.info(getCountryConfigPath() + paramName);
			getParameterRequest.withName(getCountryConfigPath() + paramName).setWithDecryption(false);
			GetParameterResult parameterResult = ssmClient.getParameter(getParameterRequest);
			log.info(TOTAL_TIME, (System.currentTimeMillis() - startTime));
			return parameterResult.getParameter().getValue();
		} catch (Exception e) {
			return null;
		}
	}

	public String getRegionConfigParameter(String paramName) {
		log.info("Looking into region config: " + paramName);
		try {
			long startTime = System.currentTimeMillis();
			GetParameterRequest getParameterRequest = new GetParameterRequest();
			getParameterRequest.withName(getRegionConfigPath() + paramName).setWithDecryption(false);
			GetParameterResult parameterResult = ssmClient.getParameter(getParameterRequest);
			log.info(TOTAL_TIME, (System.currentTimeMillis() - startTime));
			return parameterResult.getParameter().getValue();
		} catch (Exception e) {
			return null;
		}
	}

	public String getProfileConfigParameter(String paramName) {
		log.info("Looking into profile config: " + paramName);
		try {
			long startTime = System.currentTimeMillis();
			GetParameterRequest getParameterRequest = new GetParameterRequest();
			getParameterRequest.withName(getProfileConfigPath() + paramName).setWithDecryption(false);
			GetParameterResult parameterResult = ssmClient.getParameter(getParameterRequest);
			log.info(TOTAL_TIME, (System.currentTimeMillis() - startTime));
			return parameterResult.getParameter().getValue();
		} catch (Exception e) {
			return null;
		}
	}

	public String getEnvironmentParameter(String paramName) {

		String paramValue = countriesConfig.getParameter(paramName);

		if (null == paramValue) {
			paramValue = environment.getProperty(paramName);
		}
		return paramValue;
	}
}
