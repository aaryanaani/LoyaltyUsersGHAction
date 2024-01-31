package com.skechers.loyalty.users.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;
import com.skechers.loyalty.users.exception.CustomException;
import com.skechers.loyalty.users.model.CreateUser;
import com.skechers.loyalty.users.model.CreateUserResponse;
import com.skechers.loyalty.users.model.MultipleUserLookupResponse;
import com.skechers.loyalty.users.model.RegistrationPayload;
import com.skechers.loyalty.users.model.UpdateUser;
import com.skechers.loyalty.users.model.UserLookupResponse;
import com.skechers.loyalty.users.utils.ParameterUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoyaltyUsersService {

	private static final String REGISTERS_ONLINE_ACCOUNT = "registers_online_account";

	@Autowired
	private ParameterUtil parameterUtil;
	
	@Autowired
	private SessionMService sessionMService;

	public ResponseEntity<UserLookupResponse> userLookupEvent(Optional<String> email, Optional<String> phone,
			Optional<String> profile) {
		UserLookupResponse profileResponse = null;
		Instant start = Instant.now();
		String emailValue = email.isPresent() ? email.get() : null;
		String phoneValue = phone.isPresent() ? phone.get() : null;
		String profileValue = profile.isPresent() ? profile.get() : null;
		if (emailValue == null && phoneValue == null) {
			log.info("Missing required parameters: email or phone");
			throw new CustomException("Missing parameters : email OR phone is requried", HttpStatus.BAD_REQUEST);
		}
		profileResponse = sessionMService.searchUser(emailValue, phoneValue, profileValue);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.info("Time taken for searchUser  {} milliseconds", timeElapsed.toMillis());
		return ResponseEntity.status(HttpStatus.OK).body(profileResponse);
	}

	public void registrationEvents(RegistrationPayload registrationPayload) {
		Instant start = Instant.now();
		Instant end = null;
		Duration timeElapsed = null;
		String registerFlag = parameterUtil.getEnvironmentParameter(REGISTERS_ONLINE_ACCOUNT);

		if("true".equalsIgnoreCase(registerFlag)) {
			String smRegistrationEvent = sessionMService.triggerSMRegistrationEvent(registrationPayload.getUserId());
			end = Instant.now();
			timeElapsed = Duration.between(start, end);
			log.info("Time taken registrationEvents  {} milliseconds", timeElapsed.toMillis());
			log.debug("triggerSMRegistrationEvent done {} ", smRegistrationEvent);
		}
		if (registrationPayload.isWebRegistration()) {
			if(Strings.isNullOrEmpty(registrationPayload.getSubscriberKey())) {
				throw new CustomException("Missing parameters : SubscriberKey is requried", HttpStatus.BAD_REQUEST);
			}
			String smCampaignEvent = sessionMService.triggerSMCampaignEvent(registrationPayload.getSubscriberKey());
			end = Instant.now();
			timeElapsed = Duration.between(start, end);
			log.info("Time taken for webRegistration  {} milliseconds", timeElapsed.toMillis());
			log.debug("webRegistration done. {}", smCampaignEvent);
		}
	}

	public ResponseEntity<CreateUserResponse> createUser(CreateUser user) {
		Instant start = Instant.now();
		if (Strings.isNullOrEmpty(user.getUser().getEmail())) {
			log.info("Missing required parameters: email");
			throw new CustomException("Missing parameters : email is requried", HttpStatus.BAD_REQUEST);
		}

		CreateUserResponse userResponse = sessionMService.createSessionmUser(user);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		log.info("Time taken for createUser  {} milliseconds", timeElapsed.toMillis());
		return ResponseEntity.status(HttpStatus.OK).body(userResponse);
	}

	public ResponseEntity<UserLookupResponse> userLookupEventByUserId(String userId, Optional<String> profile) {
		UserLookupResponse profileResponse = null;

		Instant start = Instant.now();
		String profileValue = profile.isPresent() ? profile.get() : null;
		profileResponse = sessionMService.userLookupEventByUserId(userId, profileValue);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken In LoyaltyUsers while retrieving the UserDetails By UserId  {} milliseconds",
				timeElapsed.toMillis());
		return ResponseEntity.status(HttpStatus.OK).body(profileResponse);

	}

	public ResponseEntity<UserLookupResponse> updateUserById(String userId, UpdateUser user) {
		UserLookupResponse updateResponse = null;
		Instant start = Instant.now();
		updateResponse = sessionMService.updateUserById(userId, user);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken In LoyaltyUsers while updating the UserDetails By UserId  {} milliseconds",
				timeElapsed.toMillis());
		return ResponseEntity.status(HttpStatus.OK).body(updateResponse);

	}
	
	public ResponseEntity<String> deleteUserById(String userId) {
		String deleteResponse = null;
		Instant start = Instant.now();
		deleteResponse = sessionMService.deleteUserById(userId);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken In LoyaltyUsers while updating the UserDetails By UserId  {} milliseconds",
				timeElapsed.toMillis());
		return ResponseEntity.status(HttpStatus.OK).body(deleteResponse);

	}

	public ResponseEntity<UserLookupResponse> userDetailsByExternalId(String externalId, Optional<String> profile) {
		UserLookupResponse profileResponse = null;

		Instant start = Instant.now();
		String profileValue = profile.isPresent() ? profile.get() : null;
		profileResponse = sessionMService.userDetailsByExternalId(externalId, profileValue);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken In LoyaltyUsers while retrieving the UserDetails By ExternalId  {} milliseconds",
				timeElapsed.toMillis());
		return ResponseEntity.status(HttpStatus.OK).body(profileResponse);

	}

	public ResponseEntity <MultipleUserLookupResponse> multipleUserDetailsByProfileType(List<String> userIds, Optional<String> profile) {
		Instant start = Instant.now();
		String profileValue = profile.isPresent() ? profile.get() : null;
		MultipleUserLookupResponse profileResponse = sessionMService.multipleUserDetailsByProfileType(userIds, profileValue);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken In LoyaltyUsers while retrieving multiple User Details By ProfileType {} milliseconds",
				timeElapsed.toMillis());
		return ResponseEntity.status(HttpStatus.OK).body(profileResponse);
	}
}
