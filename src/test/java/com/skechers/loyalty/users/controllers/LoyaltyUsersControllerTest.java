package com.skechers.loyalty.users.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skechers.loyalty.users.exception.CustomException;
import com.skechers.loyalty.users.exception.LoyaltyUsersExceptionHandler;
import com.skechers.loyalty.users.model.CreateUser;
import com.skechers.loyalty.users.model.CreateUser.UserDetails;
import com.skechers.loyalty.users.model.CreateUserResponse;
import com.skechers.loyalty.users.model.MultipleUserLookupResponse;
import com.skechers.loyalty.users.model.RegistrationPayload;
import com.skechers.loyalty.users.model.UpdateUser;
import com.skechers.loyalty.users.model.UserLookupResponse;
import com.skechers.loyalty.users.service.LoyaltyUsersService;
import com.skechers.loyalty.users.service.SessionMService;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LoyaltyUsersControllerTest {

	private static final String MOCK_DATA_USERID = "b654282e-e610-11ec-84e7-d9be9a968349";

	private static final String USER_USERID_URL = "/users/b654282e-e610-11ec-84e7-d9be9a968349";

	private static final String APPLICATION_VND_LOYALTY_USER_V1_JSON = "application/vnd.loyalty.user-v1+json";

	private static final String COUNTRY = "country";

	private static final String EMAIL = "email";

	private static final String MOCK_DATA_EMAIL = "test@test.com";

	private static final String USERS_SEARCH_URL = "/users/search/";

	@Mock
	LoyaltyUsersService loyaltyUsersService;

	@Mock
	SessionMService sessionmService;

	private MockMvc mockMvc;

	@InjectMocks
	private LoyaltyUsersController loyaltyUsersController;

	@BeforeEach
	public void setup() {

		mockMvc = MockMvcBuilders.standaloneSetup(loyaltyUsersController)
				.setControllerAdvice(new LoyaltyUsersExceptionHandler()).build();
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
				.thenReturn(new ResponseEntity<UserLookupResponse>(lookupResponse, HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);

	}

	@Test
	void searchProfileWithEmailwithBadRequestResponse() throws Exception {
		String url = USERS_SEARCH_URL;
		HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		Mockito.lenient().when(
				loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(), Optional.empty()))
				.thenThrow(exception);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), output);

	}

	@Test
	void searchProfileWithEmailwithHttpMessageNotReadableException() throws Exception {
		String url = USERS_SEARCH_URL;
		HttpMessageNotReadableException exception = new HttpMessageNotReadableException("bad message",
				new MockHttpInputMessage("".getBytes()));
		Mockito.lenient().when(
				loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(), Optional.empty()))
				.thenThrow(exception);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), output);

	}

	@Test
	void searchProfileWithEmailwithCustomException() throws Exception {
		String url = USERS_SEARCH_URL;
		CustomException exception = new CustomException("custom error", HttpStatus.BAD_REQUEST);
		Mockito.lenient().when(
				loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(), Optional.empty()))
				.thenThrow(exception);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), output);

	}

	@Test
	void searchProfileWithEmailwithWebClientResponseException() throws Exception {
		String url = USERS_SEARCH_URL;
		WebClientResponseException exception = new WebClientResponseException(HttpStatus.BAD_REQUEST.value(),
				"web client error", null, "{\"message\":\"error\"}".getBytes(), null);
		Mockito.lenient().when(
				loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(), Optional.empty()))
				.thenThrow(exception);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());
		assertEquals(HttpStatus.BAD_REQUEST.value(), output);

		assertEquals("{\"message\":\"error\"}", obj.get("message").toString());

	}

	@Test
	void searchProfileWithEmailAndProfileType() throws Exception {
		String url = USERS_SEARCH_URL;
		UserLookupResponse lookupResponse = new UserLookupResponse();
		lookupResponse.setStatus("ok");
		UserLookupResponse.UserDetails details = new UserLookupResponse.UserDetails();
		
		lookupResponse.setUser(details);
		Mockito.lenient()
				.when(loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL), Optional.empty(),
						Optional.of("complete")))
				.thenReturn(new ResponseEntity<UserLookupResponse>(lookupResponse,
						HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param(EMAIL, MOCK_DATA_EMAIL)
				.param("profile", "complete").header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);
	}

	@Test
	void usersRegistationtriggerSMRegistrationEvent() throws Exception {
		String url = "/users/registration/";
		RegistrationPayload payload = new RegistrationPayload();
		payload.setUserId("6a981a62-3f13-11eb-9dd8-e4ae279e1fd7");
		payload.setSubscriberKey("62002040999");
		payload.setWebRegistration(false);
		Mockito.lenient().doNothing().when(loyaltyUsersService).registrationEvents(payload);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
				.contentType("application/vnd.loyalty.userregistration-v1+json").header(COUNTRY, "us")
				.content(asJsonString(payload));
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int outputJson = response.getStatus();
		assertEquals(HttpStatus.OK.value(), outputJson);
	}

	@Test
	void createUserWithValidData() throws Exception {
		String url = "/users/";
		CreateUser payload = new CreateUser();
		payload.setUser(new UserDetails());
		payload.getUser().setEmail(MOCK_DATA_EMAIL);
		CreateUserResponse resp = new CreateUserResponse();
		CreateUserResponse.UserDetails details = new CreateUserResponse.UserDetails();
		details.setEmail(MOCK_DATA_EMAIL);
		resp.setUser(details);
		Mockito.lenient().when(loyaltyUsersService.createUser(payload))
				.thenReturn(new ResponseEntity<CreateUserResponse>(resp, HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
				.contentType(APPLICATION_VND_LOYALTY_USER_V1_JSON).header(COUNTRY, "us")
				.content(asJsonString(payload));
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);
	}
	
	@Test
	void searchProfileWithUserId() throws Exception {
		String url = USER_USERID_URL;
		UserLookupResponse lookupResponse = new UserLookupResponse();
		lookupResponse.setStatus("ok");
		UserLookupResponse.UserDetails details = new UserLookupResponse.UserDetails();
		
		lookupResponse.setUser(details);
		Mockito.lenient().when(loyaltyUsersService.userLookupEventByUserId(MOCK_DATA_USERID, Optional.empty()))
				.thenReturn(new ResponseEntity<UserLookupResponse>(lookupResponse, HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
				.contentType(APPLICATION_VND_LOYALTY_USER_V1_JSON).header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);
	}
	
	@Test
	void updateUserWithValidData() throws Exception {
		String url = USER_USERID_URL;
		UpdateUser payload = new UpdateUser();
		
		UserLookupResponse lookupResponse = new UserLookupResponse();
		lookupResponse.setStatus("ok");
		UserLookupResponse.UserDetails details = new UserLookupResponse.UserDetails();
		
		lookupResponse.setUser(details);
		Mockito.lenient().when(loyaltyUsersService.updateUserById(MOCK_DATA_USERID, payload))
				.thenReturn(new ResponseEntity<UserLookupResponse>(lookupResponse, HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put(url)
				.contentType(APPLICATION_VND_LOYALTY_USER_V1_JSON).header(COUNTRY, "us")
				.content(asJsonString(payload));
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);
	}
	
	@Test
	void getUserDetailsWithExternalId() throws Exception {
		String url = "/users/external/ab1ubbZzFGd7qo0joq72cTJ7zW";
		UserLookupResponse lookupResponse = new UserLookupResponse();
		lookupResponse.setStatus("ok");
		UserLookupResponse.UserDetails details = new UserLookupResponse.UserDetails();
		
		lookupResponse.setUser(details);
		Mockito.lenient().when(loyaltyUsersService.userDetailsByExternalId("ab1ubbZzFGd7qo0joq72cTJ7zW", Optional.empty()))
				.thenReturn(new ResponseEntity<UserLookupResponse>(lookupResponse, HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
				.contentType(APPLICATION_VND_LOYALTY_USER_V1_JSON).header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);
	}
	
	@Test
	void getUserDetailsWithListOfUserIds() throws Exception {
		String url = "/users/";
		MultipleUserLookupResponse lookupResponse = new MultipleUserLookupResponse();
		lookupResponse.setStatus("ok");
		MultipleUserLookupResponse.MultipleUserDetails details = new MultipleUserLookupResponse.MultipleUserDetails();
		List<MultipleUserLookupResponse.MultipleUserDetails> detailsList = new ArrayList<>();
		detailsList.add(details);
		lookupResponse.setUsers(detailsList);
		List<String> userIds = new ArrayList<>();
		userIds.add(MOCK_DATA_USERID);
		Mockito.lenient().when(loyaltyUsersService.multipleUserDetailsByProfileType(userIds, Optional.empty()))
				.thenReturn(new ResponseEntity<MultipleUserLookupResponse>(lookupResponse, HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).param("userIds", MOCK_DATA_USERID)
				.contentType(APPLICATION_VND_LOYALTY_USER_V1_JSON).header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);
	}

	@Test
	void deleteUserWithId() throws Exception {
		String url = USER_USERID_URL;

		Mockito.lenient()
				.when(loyaltyUsersService.deleteUserById(MOCK_DATA_USERID))
				.thenReturn(new ResponseEntity<String>(
						"{\"status\":\"ok\",\"user\":{\"id\":\"b654282e-e610-11ec-84e7-d9be9a968349\",\"email\":\"Test@test.com\"}}",
						HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).header(COUNTRY, "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);

	}

	
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
