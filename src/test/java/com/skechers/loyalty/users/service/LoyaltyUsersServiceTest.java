package com.skechers.loyalty.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skechers.loyalty.users.exception.CustomException;
import com.skechers.loyalty.users.model.CreateUser;
import com.skechers.loyalty.users.model.CreateUserResponse;
import com.skechers.loyalty.users.model.CreateUserResponse.UserDetails;
import com.skechers.loyalty.users.model.MultipleUserLookupResponse;
import com.skechers.loyalty.users.model.MultipleUserLookupResponse.MultipleUserDetails;
import com.skechers.loyalty.users.model.RegistrationPayload;
import com.skechers.loyalty.users.model.UpdateUser;
import com.skechers.loyalty.users.model.UpdateUser.UsersDetails;
import com.skechers.loyalty.users.utils.ParameterUtil;
import com.skechers.loyalty.users.model.UserLookupResponse;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LoyaltyUsersServiceTest {

	private static final String USER_ID_NOT_FOUND = "userId not found";

	private static final String MOCK_DATA_USERID5 = "42b430d0-0f0a-11ed-8a98-845fc7cac8d1";

	private static final String MOCK_DATA_CAMPAIGN_EVENTID = "f46sf4f46";

	private static final String MOCK_DATA_USERID4 = "ddtrddr44e4";

	private static final String MOCK_DATA_USERID3 = "2b71a4d8-e2ba-11ec-9aad-7668e4122109";

	private static final String MOCK_DATA_EVENTID = "4fte5e";

	private static final String MOCK_DATA_USERID2 = "284d4f0c-600f-11eb-9893-dedffb956c9b";

	private static final String MOCK_DATA_EXTERNALID = "62002040999";

	private static final String COMPLETE = "complete";

	private static final String MOCK_DATA_PHONE = "111-222-3333";

	private static final String MOCK_DATA_USERID = "b654282e-e610-11ec-84e7-d9be9a968349";

	private static final String MOCK_DATA_EMAIL = "Test@test.com";
	
	private static final String REGISTERS_ONLINE_ACCOUNT = "registers_online_account";

	@Mock
	SessionMService sessionMService;

	@Mock
	ObjectMapper objectMapper;
	
	@Mock
	private ParameterUtil parameterUtil;

	@InjectMocks
	LoyaltyUsersService loyaltyUsersService;

	@Test
	void createUserWithValidParam() {
		CreateUserResponse generated = getCreateUserResponse();
		CreateUser createUser = new CreateUser();
		createUser.setUser(new com.skechers.loyalty.users.model.CreateUser.UserDetails());
		createUser.getUser().setEmail(MOCK_DATA_EMAIL);
		Mockito.when(sessionMService.createSessionmUser(createUser)).thenReturn(getCreateUserResponse());
		ResponseEntity<CreateUserResponse> response = loyaltyUsersService.createUser(createUser);
		CreateUserResponse fromService = response.getBody();
		assertEquals(generated, fromService);

	}

	@Test
	void createUserWithInvalidParam() {
		CreateUser createUser = new CreateUser();
		createUser.setUser(new com.skechers.loyalty.users.model.CreateUser.UserDetails());
		Exception exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.createUser(createUser);
		});

		String expectedMessage = "Missing parameters : email";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void searchUserWithEmail() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		Mockito.when(sessionMService.searchUser(MOCK_DATA_EMAIL, null, null)).thenReturn(userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL),
				Optional.empty(), Optional.empty());
		UserLookupResponse fromService = response.getBody();
		assertEquals(userLookupResponse, fromService);
	}
	
	@Test
	void searchUserWithPhone() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		
		Mockito.when(sessionMService.searchUser(null, MOCK_DATA_PHONE, null)).thenReturn(
				userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService.userLookupEvent(Optional.empty(),
				Optional.of(MOCK_DATA_PHONE), Optional.empty());
		UserLookupResponse fromService = response.getBody();
		assertEquals( fromService.getUser().getId(), userLookupResponse.getUser().getId());
	}
	
	@Test
	void searchUserWithAllParam() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		Mockito.when(sessionMService.searchUser(MOCK_DATA_EMAIL, MOCK_DATA_PHONE, COMPLETE)).thenReturn(
				userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService.userLookupEvent(Optional.of(MOCK_DATA_EMAIL),
				Optional.of(MOCK_DATA_PHONE), Optional.of(COMPLETE));
		UserLookupResponse fromService = response.getBody();
		assertEquals( fromService.getUser().getId(), userLookupResponse.getUser().getId());
	}

	@Test
	void searchUserWithNoParam() {
		Exception exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.userLookupEvent(Optional.empty(), Optional.empty(), Optional.empty());
		});

		String expectedMessage = "Missing parameters : email OR phone";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void getUserByUserIdWithUserIdOnly() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		Mockito.when(sessionMService.userLookupEventByUserId(MOCK_DATA_USERID, null)).thenReturn(userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService
				.userLookupEventByUserId(MOCK_DATA_USERID, Optional.empty());
		UserLookupResponse fromService = response.getBody();
		assertEquals( fromService.getUser().getId(), userLookupResponse.getUser().getId());
	}
	
	@Test
	void getUserByUserIdWithValidParam() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		
		Mockito.when(sessionMService.userLookupEventByUserId(MOCK_DATA_USERID, COMPLETE)).thenReturn(
				userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService
				.userLookupEventByUserId(MOCK_DATA_USERID, Optional.of(COMPLETE));
		UserLookupResponse fromService = response.getBody();
		assertEquals( fromService.getUser().getId(), userLookupResponse.getUser().getId());
	}

	@Test
	void getUserByUserIdWithInvalidParam() {
		Mockito.when(sessionMService.userLookupEventByUserId(MOCK_DATA_EVENTID, null))
				.thenThrow(new CustomException(USER_ID_NOT_FOUND, HttpStatus.BAD_REQUEST));

		CustomException exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.userLookupEventByUserId(MOCK_DATA_EVENTID, Optional.empty());
		});

		String expectedMessage = USER_ID_NOT_FOUND;
		String actualMessage = exception.getMessage();
		HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
		HttpStatus actualStatus = exception.getHttpStatus();

		assertTrue(actualMessage.contains(expectedMessage));
		assertEquals(expectedStatus, actualStatus);
	}

	@Test
	void getUserByExternalIdWithExternalIdOnly() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		Mockito.when(sessionMService.userDetailsByExternalId(MOCK_DATA_EXTERNALID, null)).thenReturn(userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService.userDetailsByExternalId(MOCK_DATA_EXTERNALID, Optional.empty());
		UserLookupResponse fromService = response.getBody();
		assertEquals( fromService.getUser().getId(), userLookupResponse.getUser().getId());
	}

	@Test
	void getUserByExternalIdWithValidParam() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		
		Mockito.when(sessionMService.userDetailsByExternalId(MOCK_DATA_EXTERNALID, COMPLETE)).thenReturn(
				userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService.userDetailsByExternalId(MOCK_DATA_EXTERNALID, Optional.of(COMPLETE));
		UserLookupResponse fromService = response.getBody();
		assertEquals( fromService.getUser().getId(), userLookupResponse.getUser().getId());
	}
	
	@Test
	void getUserByExternalIdWithInvalidParam() {
		Mockito.when(sessionMService.userDetailsByExternalId("456567", null))
				.thenThrow(new CustomException("externalId not found", HttpStatus.BAD_REQUEST));

		Exception exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.userDetailsByExternalId("456567", Optional.empty());
		});

		String expectedMessage = "externalId not found";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateUserWithValidParam() {
		UserLookupResponse userLookupResponse = new UserLookupResponse();
		userLookupResponse.setStatus("ok");
		userLookupResponse.setUser(new com.skechers.loyalty.users.model.UserLookupResponse.UserDetails());
		userLookupResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		userLookupResponse.getUser().setId(MOCK_DATA_USERID);
		Mockito.when(sessionMService.updateUserById(MOCK_DATA_USERID3, getUpdateUser()))
				.thenReturn(userLookupResponse);
		ResponseEntity<UserLookupResponse> response = loyaltyUsersService.updateUserById(MOCK_DATA_USERID3,
				getUpdateUser());
		UserLookupResponse fromService = response.getBody();
		assertEquals(userLookupResponse, fromService);
	}

	@Test
	void updateUserWithInvalidParam() {
		Mockito.when(sessionMService.updateUserById(MOCK_DATA_USERID4, getUpdateUser()))
				.thenThrow(new CustomException(USER_ID_NOT_FOUND, HttpStatus.BAD_REQUEST));

		Exception exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.updateUserById(MOCK_DATA_USERID4, getUpdateUser());
		});

		String expectedMessage = USER_ID_NOT_FOUND;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void deleteUserWithValidParam() {
		String generated = "{\"status\":\"ok\",\"user\":{\"id\":\"2b71a4d8-e2ba-11ec-9aad-7668e4122109\",\"first_name\":\"Tester\",\"last_name\":\"Testing\"}}";
		Mockito.when(sessionMService.deleteUserById(MOCK_DATA_USERID3))
				.thenReturn(
						"{\"status\":\"ok\",\"user\":{\"id\":\"2b71a4d8-e2ba-11ec-9aad-7668e4122109\",\"first_name\":\"Tester\",\"last_name\":\"Testing\"}}");
		ResponseEntity<String> response = loyaltyUsersService.deleteUserById(MOCK_DATA_USERID3);
		String fromService = response.getBody();
		JSONAssert.assertEquals(generated, fromService, JSONCompareMode.LENIENT);
	}
	
	@Test
	void deleteUserWithInvalidParam() {
		Mockito.when(sessionMService.deleteUserById(MOCK_DATA_USERID4))
				.thenThrow(new CustomException(USER_ID_NOT_FOUND, HttpStatus.BAD_REQUEST));

		Exception exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.deleteUserById(MOCK_DATA_USERID4);
		});

		String expectedMessage = USER_ID_NOT_FOUND;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void registerUserWhenWebRegistrationTrueNonNullSubscriptionKey() {
		RegistrationPayload payload = new RegistrationPayload();
		payload.setUserId(MOCK_DATA_USERID2);
		payload.setWebRegistration(true);
		payload.setSubscriberKey(MOCK_DATA_CAMPAIGN_EVENTID);
		Mockito.when(parameterUtil.getEnvironmentParameter(REGISTERS_ONLINE_ACCOUNT)).thenReturn("true");
		Mockito.when(sessionMService.triggerSMRegistrationEvent(MOCK_DATA_USERID2)).thenReturn(
				"{\"retailer_id\":\"75062048-c6ee-4137-a764-c5c3c02b4fc7\",\"user_id\":\"284d4f0c-600f-11eb-9893-dedffb956c9b\",\"event_lookup\":\"REGISTERS_ONLINE_ACCOUNT\",\"is_session_m\":\"true\"}");
		Mockito.when(sessionMService.triggerSMCampaignEvent(MOCK_DATA_CAMPAIGN_EVENTID))
				.thenReturn("{\"events\":{\"onlineacct_complete\":1}}");
		loyaltyUsersService.registrationEvents(payload);
		Mockito.verify(sessionMService, times(1)).triggerSMCampaignEvent(MOCK_DATA_CAMPAIGN_EVENTID);
	}

	@Test
	void registerUserWhenWebRegistrationTrueWithNullSubscriptionKey() {
		RegistrationPayload payload = new RegistrationPayload();
		payload.setUserId(MOCK_DATA_USERID2);
		payload.setWebRegistration(true);
		payload.setSubscriberKey(null);
		Mockito.when(parameterUtil.getEnvironmentParameter(REGISTERS_ONLINE_ACCOUNT)).thenReturn("true");
		Exception exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.registrationEvents(payload);
		});

		String expectedMessage = "Missing parameters : SubscriberKey";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void registerUserWhenWebRegistrationFalse() {
		RegistrationPayload payload = new RegistrationPayload();
		payload.setUserId(MOCK_DATA_USERID2);
		payload.setWebRegistration(false);
		payload.setSubscriberKey(MOCK_DATA_CAMPAIGN_EVENTID);
		Mockito.when(parameterUtil.getEnvironmentParameter(REGISTERS_ONLINE_ACCOUNT)).thenReturn("true");
		Mockito.when(sessionMService.triggerSMRegistrationEvent(MOCK_DATA_USERID2)).thenReturn(
				"{\"retailer_id\":\"75062048-c6ee-4137-a764-c5c3c02b4fc7\",\"user_id\":\"284d4f0c-600f-11eb-9893-dedffb956c9b\",\"event_lookup\":\"REGISTERS_ONLINE_ACCOUNT\",\"is_session_m\":\"true\"}");
		loyaltyUsersService.registrationEvents(payload);
		Mockito.verify(sessionMService, never()).triggerSMCampaignEvent(MOCK_DATA_CAMPAIGN_EVENTID);
	}

	@Test
	void multipleUserDetailsByProfileTypeWithValidParam() {
		List<String> userList = new ArrayList<String>();
		userList.add(MOCK_DATA_USERID);
		userList.add(MOCK_DATA_USERID5);
		MultipleUserLookupResponse userLookupResponse = new MultipleUserLookupResponse();
		userLookupResponse.setStatus("ok");
		List<MultipleUserDetails> userDetails = new ArrayList<>();
		MultipleUserDetails detail = new MultipleUserDetails();
		detail.setEmail(MOCK_DATA_EMAIL);
		detail.setId(MOCK_DATA_USERID);
		userDetails.add(detail);
		userLookupResponse.setUsers(userDetails);
		Mockito.when(sessionMService.multipleUserDetailsByProfileType(userList, null)).thenReturn(userLookupResponse);
		ResponseEntity<MultipleUserLookupResponse> response = loyaltyUsersService.multipleUserDetailsByProfileType(userList,
				Optional.empty());
		MultipleUserLookupResponse responseFromService = response.getBody();
		assertEquals(userLookupResponse, responseFromService);
	}

	@Test
	void multipleUserDetailsByProfileTypeComplete() {
		List<String> userList = new ArrayList<String>();
		userList.add(MOCK_DATA_USERID);
		userList.add(MOCK_DATA_USERID5);
		MultipleUserLookupResponse userLookupResponse = new MultipleUserLookupResponse();
		userLookupResponse.setStatus("ok");
		List<MultipleUserDetails> userDetails = new ArrayList<>();
		MultipleUserDetails detail = new MultipleUserDetails();
		detail.setEmail(MOCK_DATA_EMAIL);
		detail.setId(MOCK_DATA_USERID);
		userDetails.add(detail);
		userLookupResponse.setUsers(userDetails);
		Mockito.when(sessionMService.multipleUserDetailsByProfileType(userList, COMPLETE)).thenReturn(userLookupResponse);
		ResponseEntity<MultipleUserLookupResponse> response = loyaltyUsersService.multipleUserDetailsByProfileType(userList,
				Optional.of(COMPLETE));
		MultipleUserLookupResponse responseFromService = response.getBody();
		assertEquals(userLookupResponse, responseFromService);
	}

	@Test
	void multipleUserDetailsByProfileTypeWithInvalidParam() {
		List<String> userList = new ArrayList<String>();
		userList.add(MOCK_DATA_USERID);
		userList.add(MOCK_DATA_USERID5);
		Mockito.when(sessionMService.multipleUserDetailsByProfileType(userList, null))
				.thenThrow(new CustomException("User not found", HttpStatus.BAD_REQUEST));

		Exception exception = assertThrows(CustomException.class, () -> {
			loyaltyUsersService.multipleUserDetailsByProfileType(userList, Optional.empty());
		});

		String expectedMessage = "User not found";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	private CreateUserResponse getCreateUserResponse() {
		CreateUserResponse createUserResponse = new CreateUserResponse();
		createUserResponse.setUser(new UserDetails());
		createUserResponse.getUser().setEmail(MOCK_DATA_EMAIL);
		return createUserResponse;
	}

	private UpdateUser getUpdateUser() {
		UpdateUser updateUser = new UpdateUser();
		updateUser.setUser(new UsersDetails());
		updateUser.getUser().setFirstName("Tester");
		updateUser.getUser().setLastName("Testing");
		return updateUser;
	}

}
