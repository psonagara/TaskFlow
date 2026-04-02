package com.ps.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constant.IMappingConstants;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.RegisterRequest;
import com.ps.dto.response.LoginResponse;
import com.ps.service.IAuthService;
import com.ps.util.CommonUtil;

@RestController
@RequestMapping(IMappingConstants.AUTH_API_PATH)
public class AuthRestController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthRestController.class);
	
	@Autowired
	private IAuthService authService;

	@PostMapping(IMappingConstants.REGISTER_PATH)
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		LOG.info("Register Request Received: " + request);
		String message = authService.register(request);
		return CommonUtil.prepareResponseWithMessage(message, HttpStatus.OK);
	}
	
	@PostMapping(IMappingConstants.LOGIN_PATH)
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		LOG.info("Login Request Received: " + request);
		LoginResponse response = authService.login(request);
		return CommonUtil.prepareResponseWithContent(response, HttpStatus.OK);
	}
}
