package com.skechers.loyalty.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skechers.loyalty.users.service.LoyaltyUsersService;

@Profile({"dev","local","staging"})
@RestController
@RequestMapping(value = "/testusers")
public class LoyaltyUsersDeleteController {
	
	@Autowired
	LoyaltyUsersService loyaltyUsersService;

	
	@DeleteMapping(value = "/{userId}")
	public ResponseEntity<String> deleteUserById(@PathVariable String userId) {
	
		return loyaltyUsersService.deleteUserById(userId);
	}
}
