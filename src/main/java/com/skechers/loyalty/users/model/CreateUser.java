package com.skechers.loyalty.users.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreateUser {

	@JsonProperty("user")
	private UserDetails user;

	@Data
	public static class UserDetails {

		@JsonProperty("email")
		private String email;

		@JsonProperty("external_id")
		private String externalId;

		@JsonProperty("auth_token")
		private boolean authToken;

		@JsonProperty("identifiers")
		private List<Identifiers> identifiers;

		@Data
		private static class Identifiers {

			@JsonProperty("external_id")
			private String externalId;

			@JsonProperty("external_id_type")
			private String externalIdType;
		}

		@JsonProperty("opted_in")
		private boolean optedIn;

		@JsonProperty("external_id_type")
		private String externalIdType;

		@JsonProperty("first_name")
		private String firstName;

		@JsonProperty("last_name")
		private String lastName;

		@JsonProperty("phone_numbers")
		private List<PhoneNumbers> phoneNumbers;

		@Data
		private static class PhoneNumbers {

			@JsonProperty("phone_number")
			private String phoneNumber;

			@JsonProperty("phone_type")
			private String phoneType;
		}

		@JsonProperty("user_profile")
		private UserProfile userProfile;

		@Data
		private static class UserProfile {

			@JsonProperty("email_opt_in")
			private boolean emailOptIn;

			@JsonProperty("signup_channel")
			private String signupChannel;

			@JsonProperty("user_status")
			private String userStatus;

			@JsonProperty("store_no")
			private Long storeNo;

			@JsonProperty("registration_date")
			private String registrationDate;
			
			@JsonProperty("is_undeliverable")
			private boolean isUndeliverable;
		}
		
		@JsonProperty("dob")
		private String dob;

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
	}

}