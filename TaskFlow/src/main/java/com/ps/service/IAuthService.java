package com.ps.service;

import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.RegisterRequest;
import com.ps.dto.response.LoginResponse;

public interface IAuthService {

	public String register(RegisterRequest request);
	public LoginResponse login(LoginRequest request);
	
}
