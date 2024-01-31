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
import org.springframework.test.util.ReflectionTestUtils;
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
class ApplicationStatusControllerTest {
	
	private MockMvc mockMvc;

	@InjectMocks
	private ApplicationStatusController applicationStatusController;

	@BeforeEach
	public void setup() {

		mockMvc = MockMvcBuilders.standaloneSetup(applicationStatusController)
				.setControllerAdvice(new LoyaltyUsersExceptionHandler()).build();
	}

	@Test
	void appStatus() throws Exception {
		String url = "/application-status";
		ReflectionTestUtils.setField(applicationStatusController, "env", "test");
		ReflectionTestUtils.setField(applicationStatusController, "region", "test-region");

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).header("country", "us");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		int output = response.getStatus();
		assertEquals(HttpStatus.OK.value(), output);

	}


}
