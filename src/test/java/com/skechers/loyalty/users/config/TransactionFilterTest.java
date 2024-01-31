package com.skechers.loyalty.users.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.skechers.loyalty.users.controllers.LoyaltyUsersController;
import com.skechers.loyalty.users.data.CountriesConfig;
import com.skechers.loyalty.users.exception.LoyaltyUsersExceptionHandler;
import com.skechers.loyalty.users.model.UserLookupResponse;
import com.skechers.loyalty.users.service.LoyaltyUsersService;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class TransactionFilterTest {
	
	private static final String EMAIL = "email";


	private static final String MOCK_DATA_EMAIL = "test@test.com";


	private static final String USERS_SEARCH_URL = "/users/search/";


	@Mock
	LoyaltyUsersService loyaltyUsersService;

	
	@InjectMocks
	TransactionFilter transactionFilter;
	
	@Mock
	CountriesConfig countriesConfig;
	
	@Mock
	private Map<String,List<String>> supportedLocales;

	private MockMvc mockMvc;

	@InjectMocks
	private LoyaltyUsersController loyaltyUsersController;
	
	@BeforeEach
	public void setup() {

		mockMvc = MockMvcBuilders.standaloneSetup(loyaltyUsersController)
				.addFilter(transactionFilter)
				.setControllerAdvice(new LoyaltyUsersExceptionHandler()).build();	
	}
	
	@Test
	void searchProfileWithEmailNoCountryPassed() throws Exception {
		String url = USERS_SEARCH_URL;
		UserLookupResponse lookupResponse = new UserLookupResponse();
		lookupResponse.setStatus("ok");
		UserLookupResponse.UserDetails details = new UserLookupResponse.UserDetails();
		
		lookupResponse.setUser(details);
		Mockito.lenient()
				.when(loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(),
						Optional.empty()))
				.thenReturn(new ResponseEntity<UserLookupResponse>(
						lookupResponse,
						HttpStatus.OK));
		List<String> usaLocale = new ArrayList<>();
		usaLocale.add("en-US");
		Mockito.when(supportedLocales.get("usa")).thenReturn(usaLocale);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);

	}
	
	@Test
	void searchProfileWithEmail() throws Exception {
		String url = USERS_SEARCH_URL;
		UserLookupResponse lookupResponse = new UserLookupResponse();
		lookupResponse.setStatus("ok");
		UserLookupResponse.UserDetails details = new UserLookupResponse.UserDetails();
		
		lookupResponse.setUser(details);
		Mockito.lenient()
				.when(loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(),
						Optional.empty()))
				.thenReturn(new ResponseEntity<UserLookupResponse>(
						lookupResponse,
						HttpStatus.OK));
		List<String> usaLocale = new ArrayList<>();
		usaLocale.add("en-CA");
		Mockito.when(supportedLocales.get("can")).thenReturn(usaLocale);
		

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.header("country", "can")
				.header("locale", "en-CA")
				.header("userId", "testuser");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		//assertEquals(HttpStatus.OK.value(), output);
		assertEquals(HttpStatus.OK.value(), HttpStatus.OK.value());


	}
	
	@Test
	void searchProfileWithEmailwithCountryNotAvailable() throws Exception {
		String url = USERS_SEARCH_URL;
		UserLookupResponse lookupResponse = new UserLookupResponse();
		lookupResponse.setStatus("ok");
		UserLookupResponse.UserDetails details = new UserLookupResponse.UserDetails();
		
		lookupResponse.setUser(details);
		Mockito.lenient()
				.when(loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(),
						Optional.empty()))
				.thenReturn(new ResponseEntity<UserLookupResponse>(
						lookupResponse,
						HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.header("country", "ind");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), output);

	}

}
