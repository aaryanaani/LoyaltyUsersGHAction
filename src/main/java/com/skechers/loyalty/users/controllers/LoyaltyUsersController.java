package com.skechers.loyalty.users.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skechers.loyalty.users.exception.CustomException;
import com.skechers.loyalty.users.model.CreateUser;
import com.skechers.loyalty.users.model.CreateUserResponse;
import com.skechers.loyalty.users.model.MultipleUserLookupResponse;
import com.skechers.loyalty.users.model.RegistrationPayload;
import com.skechers.loyalty.users.model.UpdateUser;
import com.skechers.loyalty.users.model.UserLookupResponse;
import com.skechers.loyalty.users.service.LoyaltyUsersService;
import com.skechers.loyalty.users.service.SessionMService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping(value = "/users")
@Slf4j
public class LoyaltyUsersController {

	@Autowired
	LoyaltyUsersService loyaltyUsersService;

	@Autowired
	SessionMService sessionmService;

	private static final String SUCCESS_MESSAGE = "{\"message\": \"success\"}";

	@GetMapping(value = "/search", produces = "application/vnd.loyalty.usersearch-v1+json")
	public ResponseEntity<UserLookupResponse> userLookup(@RequestParam("email") Optional<String> email,
			@RequestParam("phone") Optional<String> phone, @RequestParam("profile") Optional<String> profileType) {
		log.debug("from /search");
		return loyaltyUsersService.userLookupEvent(email, phone, profileType);
	}

	@PostMapping(value = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> registration(@RequestBody RegistrationPayload registrationPayload) {
		log.debug("from /registration");
		loyaltyUsersService.registrationEvents(registrationPayload);
		return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_MESSAGE);
	}

	@PostMapping(produces = "application/vnd.loyalty.user-v1+json")
	public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUser user) {
		log.debug("from /users");
		return loyaltyUsersService.createUser(user);
	}

	@GetMapping(value = "/{userId}", produces = "application/vnd.loyalty.user-v1+json")
	public ResponseEntity<UserLookupResponse> userEventByUserId(@PathVariable String userId,
			@RequestParam("profile") Optional<String> profileType) {
		log.debug("from /users");
		return loyaltyUsersService.userLookupEventByUserId(userId, profileType);
	}

	@PutMapping(value = "/{userId}", produces = "application/vnd.loyalty.user-v1+json")
	public ResponseEntity<UserLookupResponse> updateUserById(@PathVariable String userId, @RequestBody UpdateUser user) {
		log.debug("from /users/{userId}");
		return loyaltyUsersService.updateUserById(userId, user);
	}

	@GetMapping(value = "/external/{externalId}", produces = "application/vnd.loyalty.user-v1+json")
	public ResponseEntity<UserLookupResponse> userDetailsByExternalId(@PathVariable String externalId,
			@RequestParam("profile") Optional<String> profileType) throws CustomException {
		log.debug("from /externalusers");
		return loyaltyUsersService.userDetailsByExternalId(externalId, profileType);
	}

	@GetMapping(produces = "application/vnd.loyalty.users-v1+json")
	public ResponseEntity<MultipleUserLookupResponse> multipleUserDetailsByProfileType(@RequestParam List<String> userIds,
			@RequestParam Optional<String> profile) {
		log.debug("from /users/");
		return loyaltyUsersService.multipleUserDetailsByProfileType(userIds, profile);

	}
	
	@DeleteMapping(value = "/{userId}")
	public ResponseEntity<String> deleteUserById(@PathVariable String userId) {
	
		return loyaltyUsersService.deleteUserById(userId);
	}
	
}
