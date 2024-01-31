package com.skechers.loyalty.users.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationPayload {

	String subscriberKey;
	String userId;
	boolean webRegistration;

}
