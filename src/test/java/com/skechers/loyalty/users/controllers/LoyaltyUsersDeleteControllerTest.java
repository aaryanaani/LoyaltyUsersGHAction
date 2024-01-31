package com.skechers.loyalty.users.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.skechers.loyalty.users.exception.LoyaltyUsersExceptionHandler;
import com.skechers.loyalty.users.service.LoyaltyUsersService;
import com.skechers.loyalty.users.service.SessionMService;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LoyaltyUsersDeleteControllerTest {
	
	@Mock
	LoyaltyUsersService loyaltyUsersService;
 
	@Mock
	SessionMService sessionmService;

	private MockMvc mockMvc;

	@InjectMocks
	private LoyaltyUsersDeleteController loyaltyUsersDeleteController;

	@BeforeEach
	public void setup() {

		mockMvc = MockMvcBuilders.standaloneSetup(loyaltyUsersDeleteController)
				.setControllerAdvice(new LoyaltyUsersExceptionHandler()).build();
	}

	@Test
	void deleteUserWithId() throws Exception {
		String url = "/testusers/b654282e-e610-11ec-84e7-d9be9a968349";

		Mockito.lenient()
				.when(loyaltyUsersService.deleteUserById("b654282e-e610-11ec-84e7-d9be9a968349"))
				.thenReturn(new ResponseEntity<String>(
						"{\"status\":\"ok\",\"user\":{\"id\":\"b654282e-e610-11ec-84e7-d9be9a968349\",\"email\":\"Test@test.com\"}}",
						HttpStatus.OK));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).header("country", "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);

	}


}
