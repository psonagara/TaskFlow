package com.ps.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.RegisterRequest;
import com.ps.dto.response.LoginResponse;
import com.ps.filter.SecurityFilter;
import com.ps.service.IAuthService;
import com.ps.util.RestUtil;
import com.ps.util.TestDataUtil;

@WebMvcTest(value =  AuthRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private IAuthService authService;

	@Test
	void testRegister() throws JsonProcessingException, Exception {
		RegisterRequest registerRequest = TestDataUtil.getRegisterRequest();
		
		when(authService.register(registerRequest)).thenReturn(IResponseConstants.REGISTRATION_SUCCESS);
		
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(registerRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.REGISTRATION_SUCCESS));
	}
	
	@Test
	void testLogin() throws JsonProcessingException, Exception {
		LoginRequest loginRequest = TestDataUtil.getLoginRequest();
		LoginResponse loginResponse = TestDataUtil.getLoginResponse();
		
		when(authService.login(loginRequest)).thenReturn(loginResponse);
		
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(loginRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.token").value(TestDataUtil.getJwtToken()))
		.andExpect(jsonPath("$.content.user.id").value(loginResponse.getUser().getId()))
		.andExpect(jsonPath("$.content.user.email").value(loginResponse.getUser().getEmail()));
	}
	
	
}
