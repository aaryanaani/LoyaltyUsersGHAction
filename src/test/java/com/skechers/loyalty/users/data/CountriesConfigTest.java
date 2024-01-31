package com.skechers.loyalty.users.data;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CountriesConfigTest {
	
	private static final String SESSIONM_SPLUS_API_KEY = "sessionm_splus_api_key";
	private static final String SESSIONM_HOSTNAME = "sessionm_hostname";
	private static final String MOCK_DATA_HOST = "localhost";
	@InjectMocks
	CountriesConfig countriesConfig;
	
	@BeforeAll
	static void setupThreadContext() {
		MDC.put("countryCode", "us");
	}
	
	@Test
	void setParameterWhenConfigIsNull() {
		countriesConfig.setParameter("us", SESSIONM_HOSTNAME, MOCK_DATA_HOST);
		assertEquals(MOCK_DATA_HOST, countriesConfig.getParameter(SESSIONM_HOSTNAME));
	}
	
	@Test
	void setParameterWhenConfigIsNotNull() {
		countriesConfig.setParameter("us", SESSIONM_HOSTNAME, MOCK_DATA_HOST);
		countriesConfig.setParameter("us", SESSIONM_SPLUS_API_KEY, "xyz");
		assertEquals("xyz", countriesConfig.getParameter(SESSIONM_SPLUS_API_KEY));
	}
	
	@Test
	void getParameterWhenConfigIsNull() {
		assertEquals(null, countriesConfig.getParameter(SESSIONM_SPLUS_API_KEY));
	}
	
	@Test
	void testIsCountryNotAvaibleWhenConfigIsNull() {
		assertEquals(true, countriesConfig.isCountryNotAvailable("us"));
	}

	@Test
	void testIsCountryNotAvaibleWhenConfigIsNotNull() {
		countriesConfig.setParameter("us", SESSIONM_HOSTNAME, MOCK_DATA_HOST);
		assertEquals(false, countriesConfig.isCountryNotAvailable("us"));
	}
}
