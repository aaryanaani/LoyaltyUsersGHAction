package com.skechers.loyalty.users.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(value=Include.NON_NULL)
public class UpdateUser {
	
	@JsonProperty("user")
	private UsersDetails user;

	@Data
	@JsonInclude(value=Include.NON_NULL)
	public static class UsersDetails {
		
		@JsonProperty("external_id")
		private String externalId;

		@JsonProperty("auth_token")
		private Boolean authToken;

		@JsonProperty("identifiers")
		private List<Identifiers> identifiers;

		@Data
		@JsonInclude(value=Include.NON_NULL)
		private static class Identifiers {

			@JsonProperty("external_id")
			private String externalId;

			@JsonProperty("external_id_type")
			private String externalIdType;
		}

		@JsonProperty("opted_in")
		private Boolean optedIn;
		
		@JsonProperty("dob")
		private String dob;

		@JsonProperty("external_id_type")
		private String externalIdType;

		@JsonProperty("first_name")
		private String firstName;

		@JsonProperty("last_name")
		private String lastName;

		@JsonProperty("phone_numbers")
		private List<PhoneNumbers> phoneNumbers;

		@Data
		@JsonInclude(value=Include.NON_NULL)
		private static class PhoneNumbers {

			@JsonProperty("phone_number")
			private String phoneNumber;

			@JsonProperty("phone_type")
			private String phoneType;
			
			@JsonProperty("preference_flags")
			private List<String> preferenceFlags;
		}

		@JsonProperty("user_profile")
		private UserProfile userProfile;

		@Data
		@JsonInclude(value=Include.NON_NULL)
		private static class UserProfile {

			@JsonProperty("email_opt_in")
			private Boolean emailOptIn;

			@JsonProperty("signup_channel")
			private String signupChannel;

			@JsonProperty("user_status")
			private String userStatus;

			@JsonProperty("store_no")
			private Long storeNo;

			@JsonProperty("registration_date")
			private String registrationDate;
		}

		@JsonProperty("address")
		private String address;

		@JsonProperty("address2")
		private String address2;

		@JsonProperty("city")
		private String city;

		@JsonProperty("zip")
		private String zip;

		@JsonProperty("state")
		private String state;

		@JsonProperty("country")
		private String country;
		
		@JsonProperty("locale")
		private String locale;
	}
	
}
