package com.skechers.loyalty.users.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.skechers.loyalty.users.data.CountriesConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	
	private AWSSimpleSystemsManagement ssmClient;

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		log.info("Loading properties..");

		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		CountriesConfig countriesConfig = new CountriesConfig();
		Map<String, Object> propertySource = new HashMap<>();
	    String loyaltyRegion = environment.getProperty("loyalty.region");
	    if(!StringUtils.hasLength(loyaltyRegion)) {	
			 loyaltyRegion = "nora";
	    }
		String[] regions = loyaltyRegion.split(",");
		for (String region : regions) {
			log.info("Loading properties.." + region);
			loadRegionProperties(region, applicationContext, countriesConfig, propertySource);
		}
		applicationContext.getBeanFactory().registerSingleton("countriesConfig", countriesConfig);
		environment.getPropertySources().addFirst(new MapPropertySource("AWS_Properties", propertySource));

	}

	private void loadRegionProperties(String region, ConfigurableApplicationContext applicationContext,
			CountriesConfig countriesConfig, Map<String, Object> propertySource) {
		if(ssmClient==null) {
			ssmClient = AWSSimpleSystemsManagementClientBuilder.standard().build();
		}
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		String path = environment.getProperty("aws.paramstore.prefix") + "/"
				+ environment.getProperty("aws.paramstore.name")
				+ environment.getProperty("aws.paramstore.profileSeparator")
				+ environment.getProperty("spring.profiles.active") + "/" + region;

		GetParametersByPathRequest getParametersByPathRequest = new GetParametersByPathRequest();
		getParametersByPathRequest.withPath(path).withWithDecryption(Boolean.TRUE).setRecursive(Boolean.TRUE);

		GetParametersByPathResult result = ssmClient.getParametersByPath(getParametersByPathRequest);
		processResult(result, propertySource, countriesConfig);
		while (result.getNextToken() != null) {
			getParametersByPathRequest.setNextToken(result.getNextToken());
			result = ssmClient.getParametersByPath(getParametersByPathRequest);
			processResult(result, propertySource, countriesConfig);
		}

	}

	private void processResult(GetParametersByPathResult result, Map<String, Object> propertySource,
			CountriesConfig countriesConfig) {
		for (Parameter param : result.getParameters()) {
			log.debug("Initialize - {}", param.getName());
			String paramName = getParamName(param.getName());
			if (paramName != null && paramName.startsWith("__")) {
				countriesConfig.setParameter(getParentNode(param.getName()), paramName.replaceFirst("__", ""),
						param.getValue());
			} else {
				propertySource.put(paramName, param.getValue());
			}
		}
	}

	private String getParamName(String path) {
		if (StringUtils.hasLength(path)) {
			String nodes[] = path.split("/");

			return nodes[nodes.length - 1];
		}
		return null;
	}

	private String getParentNode(String path) {
		if (StringUtils.hasLength(path)) {
			String nodes[] = path.split("/");

			return nodes[nodes.length - 2];
		}
		return null;
	}

	public void setSsmClient(AWSSimpleSystemsManagement ssmClient) {
		this.ssmClient = ssmClient;
	}
	
	

}
