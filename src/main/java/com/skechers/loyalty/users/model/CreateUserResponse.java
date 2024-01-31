package com.skechers.loyalty.users.model;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(value=Include.NON_NULL)
public class CreateUserResponse {

	@JsonProperty("status")
	private String status;

	@JsonProperty("user")
	private UserDetails user;

	@Data
	@JsonInclude(value=Include.NON_NULL)
	public static class UserDetails {

		@JsonProperty("id")
		private String id;

		@JsonProperty("external_id")
		private String externalId;

		@JsonProperty("opted_in")
		private boolean optedIn;

		@JsonProperty("activated")
		private boolean activated;

		@JsonProperty("proxy_ids")
		private List<String> proxyIds;

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

		@JsonProperty("available_points")
		private BigDecimal availablePoints;

		@JsonProperty("test_points")
		private BigDecimal testPoints;

		@JsonProperty("unclaimed_achievement_count")
		private BigDecimal unclaimedAchievementCount;

		@JsonProperty("email")
		private String email;

		@JsonProperty("created_at")
		private String createdAt;

		@JsonProperty("updated_at")
		private String updatedAt;
		
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

		@JsonProperty("country")
		private String country;

		@JsonProperty("suspended")
		private boolean suspended;

		@JsonProperty("last_name")
		private String lastName;

		@JsonProperty("first_name")
		private String firstName;

		@JsonProperty("registered_at")
		private String registeredAt;

		@JsonProperty("profile_photo_url")
		private String profilePhotoUrl;

		@JsonProperty("test_account")
		private boolean testAccount;

		@JsonProperty("account_status")
		private String accountStatus;

		@JsonProperty("tier")
		private String tier;

		@JsonProperty("tier_system")
		private String tierSystem;

		@JsonProperty("tier_points")
		private BigDecimal tierPoints;
		
		@JsonProperty("next_tier_points")
		private BigDecimal nextTierPoints;
		
		@JsonProperty("tier_ends_value")
		private BigDecimal tierEndsValue;

		@JsonProperty("tier_entered_at")
		private String tierEnteredAt;

		@JsonProperty("tier_resets_at")
		private String tierResetsAt;

		@JsonProperty("maintenance_required_points")
		private BigDecimal maintenanceRequiredPoints;

		@JsonProperty("maintenance_points")
		private BigDecimal maintenancePoints;

		@JsonProperty("referrer_code")
		private String referrerCode;

		@JsonProperty("user_profile")
		private UserProfile userProfile;

		@Data
		@JsonInclude(value=Include.NON_NULL)
		private static class UserProfile {

			@JsonProperty("_version")
			private int version;

			@JsonProperty("registration_date")
			private String registrationDate;
			
			@JsonProperty("locale")
			private String locale;

			@JsonProperty("email_opt_in")
			private boolean emailOptIn;

			@JsonProperty("signup_channel")
			private String signupChannel;

			@JsonProperty("store_no")
			private Long storeNo;

			@JsonProperty("user_status")
			private String userStatus;
			
			@JsonProperty("is_undeliverable")
			private boolean isUndeliverable;
		}

		@JsonProperty("auth_token")
		private String authToken;

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

			@JsonProperty("verified_ownership")
			private boolean verifiedOwnership;
		}
	}

}