 package com.skechers.loyalty.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import com.skechers.loyalty.users.model.CreateUser;
import com.skechers.loyalty.users.model.CreateUserResponse;
import com.skechers.loyalty.users.model.CreateUserResponse.UserDetails;
import com.skechers.loyalty.users.model.MultipleUserLookupResponse;
import com.skechers.loyalty.users.model.UpdateUser;
import com.skechers.loyalty.users.model.UpdateUser.UsersDetails;
import com.skechers.loyalty.users.model.UserLookupResponse;
import com.skechers.loyalty.users.utils.ParameterUtil;

import reactor.core.publisher.Mono;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class SessionMServiceTest {}
