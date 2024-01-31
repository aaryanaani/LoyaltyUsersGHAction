package com.skechers.loyalty.users.service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import com.skechers.loyalty.users.model.CreateUser;
import com.skechers.loyalty.users.model.CreateUserResponse;
import com.skechers.loyalty.users.model.MultipleUserLookupResponse;
import com.skechers.loyalty.users.model.UpdateUser;
import lombok.Setter;
import com.skechers.loyalty.users.model.UserLookupResponse;
import com.skechers.loyalty.users.utils.ParameterUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Setter
public class SessionMService {

	private static final String DEBUG_LOG_ENDPOINT = "endpoint == {} ";

	public static final String PROFILE_TYPE_COMPLETE = "complete";

	private WebClient webClient;

	@Autowired
	private ParameterUtil parameterUtil;

	@Value("${spring.profiles.active}")
	private String environment;
	
	@Value("${search_user_api}")
	private String searchUserApi;
	
	@Value("${offers_api}")
	private String offersApi;
	
	@Value("${user_api}")
	private String userApi;
	
	@Value("${external_user_api}")
	private String externalUserApi;
	
	@Value("${registration_event_api}")
	private String registrationEventApi;
	
	@Value("${campaign_api}")
	private String campaignApi;

	private static String HOSTNAME = "sessionm_hostname";
	private static String DOMAIN_HOSTNAME = "sessionm_domain_hostname";
	private static String SESSIONM_SPLUS_API_KEY = "sessionm_splus_api_key";
	private static String SESSIONM_SPLUS_API_SECRET = "sessionm_splus_api_secret";
	private static String SESSIONM_SPLUS_OFFER_API_KEY = "sessionm_splus_offers_api_key";
	private static String SESSIONM_SPLUS_OFFER_API_SECRET = "sessionm_splus_offers_api_secret";
	private static String SESSIONM_SPLUS_RETAILER_ID = "sessionm_splus_retailer_id";
	private static String ONLINE_ACCOUNT_CREATION_ID = "online_account_creation_id";
	
	public SessionMService() {
		final java.util.function.Consumer<ClientCodecConfigurer> consumer = configurer -> {
			final ClientCodecConfigurer.ClientDefaultCodecs codecs = configurer.defaultCodecs();
			codecs.maxInMemorySize(2 * 1024 * 1024);
		};
		webClient = WebClient.builder().exchangeStrategies(ExchangeStrategies.builder().codecs(consumer).build())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
	}

	public UserLookupResponse searchUser(String email, String phone, String profile) {

		Instant start = Instant.now();
		UserLookupResponse respMap = null;
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String endPoint = MessageFormat.format(searchUserApi, hostname, apiKey);
		
		log.info(parameterUtil.getEnvironmentParameter("aProfile"));
		
		if (email != null && phone != null)
			endPoint = endPoint + "&email=" + email + "&phone=" + phone;
		else if (phone != null)
			endPoint = endPoint + "&mobile_number=" + phone;
		else if (email != null)
			endPoint = endPoint + "&email=" + email;

		if (profile != null && PROFILE_TYPE_COMPLETE.equals(profile)) {
			endPoint = endPoint + "&show_identifiers=true&user[user_profile]=true&expand_incentives=true";
		}
		
		Mono<ResponseEntity<UserLookupResponse>> response = webClient.get().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).retrieve().toEntity(UserLookupResponse.class);
		if (response.block() != null) {
			respMap = response.block().getBody();
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.info("Time taken for searchUser  {} milliseconds", timeElapsed.toMillis());
		return respMap;

	}

	public String triggerSMRegistrationEvent(String userId) {

		Instant start = Instant.now();
		String domainHostname = parameterUtil.getEnvironmentParameter(DOMAIN_HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_OFFER_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_OFFER_API_SECRET);
		String retailerId = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_RETAILER_ID);
		String endPoint = MessageFormat.format(registrationEventApi, domainHostname);

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("retailer_id", retailerId);
		jsonObj.put("user_id", userId);
		jsonObj.put("event_lookup", "REGISTERS_ONLINE_ACCOUNT");
		jsonObj.put("is_session_m", "true");
		Mono<String> response = webClient.post().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).bodyValue(jsonObj.toString()).retrieve()
				.bodyToMono(String.class);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.info("Time taken for triggerSMRegistrationEvent  {} milliseconds", timeElapsed.toMillis());
		return response.block();

	}

	public String triggerSMCampaignEvent(String subscriberKey) {

		Instant start = Instant.now();
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String onlineAccountCreationId = parameterUtil.getEnvironmentParameter(ONLINE_ACCOUNT_CREATION_ID);
		String endPoint = MessageFormat.format(campaignApi, hostname, apiKey, subscriberKey, onlineAccountCreationId);
		
		JSONObject eventObj = new JSONObject();
		eventObj.put("onlineacct_complete", 1);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("events", eventObj);
		log.info("triggerSMCampaignEvent {}", jsonObj);

		Mono<ResponseEntity<String>> response = webClient.post().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).bodyValue(jsonObj.toString()).retrieve()
				.toEntity(String.class);
		String retres = null;
		ResponseEntity<String> res = response.block();
		if (res.getStatusCode().is2xxSuccessful()) {
			retres = res.getBody();
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.info("Time taken for triggerSMCampaignEvent  {} milliseconds", timeElapsed.toMillis());
		return retres;

	}

	public CreateUserResponse createSessionmUser(CreateUser user) {

		Instant start = Instant.now();
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String endPoint = MessageFormat.format(userApi, hostname, apiKey);

		
		Mono<CreateUserResponse> response = webClient.post().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).bodyValue(user)
				.retrieve().bodyToMono(CreateUserResponse.class);
		CreateUserResponse responsePayload = response.block();
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.info("Time taken for createUserFromSessionM  {} milliseconds", timeElapsed.toMillis());
		log.debug("response = {}", responsePayload);
		return responsePayload;

	}

	public UserLookupResponse userLookupEventByUserId(String userId, String profileValue) {

		Instant start = Instant.now();
		
		UserLookupResponse respMap = null;
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String endPoint = MessageFormat.format(userApi, hostname, apiKey);

		if (userId != null) {
			endPoint = endPoint + userId;
		}
		if (profileValue != null && PROFILE_TYPE_COMPLETE.equals(profileValue)) {
			endPoint = endPoint + "?" + "show_identifiers=true&user[user_profile]=true&expand_incentives=true";
		}
		log.debug(DEBUG_LOG_ENDPOINT, endPoint);
		Mono<ResponseEntity<UserLookupResponse>> response = webClient.get().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).retrieve()
				.toEntity(UserLookupResponse.class);
		if (response.block() != null) {
			respMap = response.block().getBody();
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken in sessionM while retrieving the UserDetails By UserId  {} milliseconds",
				timeElapsed.toMillis());
		return respMap;

	}

	public UserLookupResponse updateUserById(String userId, UpdateUser user) {
		UserLookupResponse result = null;

		Instant start = Instant.now();
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String endPoint = MessageFormat.format(userApi, hostname, apiKey);
		
		endPoint += userId;
		log.debug("url == {} ", endPoint);
		Mono<UserLookupResponse> response = webClient.put().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).bodyValue(user)
				.retrieve().bodyToMono(UserLookupResponse.class);
		result = response.block();
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken in sessionM while updating the UserDetails By UserId  {} milliseconds",
				timeElapsed.toMillis());
		return result;
	}
	
	public String deleteUserById(String userId) {
		String result = "";

		Instant start = Instant.now();
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String endPoint = MessageFormat.format(userApi, hostname, apiKey);
		
		endPoint += userId;
		log.debug("url == {} ", endPoint);
		Mono<String> response = webClient.delete().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret))
				.retrieve().bodyToMono(String.class);
		result = response.block();
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken in sessionM while updating the UserDetails By UserId  {} milliseconds",
				timeElapsed.toMillis());
		return result;
	}

	public UserLookupResponse userDetailsByExternalId(String externalId, String profileValue) {

		Instant start = Instant.now();
		UserLookupResponse respMap = null;
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String endPoint = MessageFormat.format(externalUserApi, hostname, apiKey);
		
		if (externalId != null) {
			endPoint = endPoint + externalId;
		}
		if (profileValue != null && profileValue.equals(PROFILE_TYPE_COMPLETE)) {
			endPoint = endPoint + "?" + "show_identifiers=true&user[user_profile]=true&expand_incentives=true";
		}
		log.debug(DEBUG_LOG_ENDPOINT, endPoint);
		Mono<ResponseEntity<UserLookupResponse>> response = webClient.get().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).retrieve()
				.toEntity(UserLookupResponse.class);
		if (response.block() != null) {
			respMap = response.block().getBody();
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken in sessionM while retrieving the UserDetails By externalId  {} milliseconds",
				timeElapsed.toMillis());
		return respMap;

	}

	public MultipleUserLookupResponse multipleUserDetailsByProfileType(List<String> userIds, String profile) {
		Instant start = Instant.now();
		MultipleUserLookupResponse respMap = null;
		String hostname = parameterUtil.getEnvironmentParameter(HOSTNAME);
		String apiKey = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_KEY);
		String apiSecret = parameterUtil.getEnvironmentParameter(SESSIONM_SPLUS_API_SECRET);
		String endPoint = MessageFormat.format(userApi, hostname, apiKey);
		endPoint = endPoint + "?";
		for (String userId : userIds) {
			if (userId != null) {
				endPoint = endPoint + "user_ids[]=" + userId + "&";
			}
		}

		if (endPoint.charAt(endPoint.length() - 1) == '&') {
			endPoint = endPoint.substring(0, endPoint.length() - 1);
		}
		if (profile != null && profile.equals(PROFILE_TYPE_COMPLETE)) {
			endPoint = endPoint + "&" + "user[user_profile]=true&show_identifiers=true";
		}
		log.debug(DEBUG_LOG_ENDPOINT, endPoint);
		Mono<ResponseEntity<MultipleUserLookupResponse>> response = webClient.get().uri(endPoint)
				.headers(headers -> headers.setBasicAuth(apiKey, apiSecret)).retrieve()
				.toEntity(MultipleUserLookupResponse.class);
		if (response.block() != null) {
			respMap = response.block().getBody();
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		log.debug("Time taken in sessionM while retrieving multiple User Details By ProfileType  {} milliseconds",
				timeElapsed.toMillis());
		return respMap;
	}

}
