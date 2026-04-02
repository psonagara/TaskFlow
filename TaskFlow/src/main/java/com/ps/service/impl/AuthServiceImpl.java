package com.ps.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ps.constant.IExceptionConstants;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.RegisterRequest;
import com.ps.dto.response.LoginResponse;
import com.ps.entity.User;
import com.ps.exception.AuthException;
import com.ps.helper.AuthMappingHelper;
import com.ps.helper.ProjectMappingHelper;
import com.ps.repo.UserRepository;
import com.ps.service.IAuthService;
import com.ps.util.JwtUtil;

@Service
public class AuthServiceImpl implements IAuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public String register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new AuthException(IExceptionConstants.USER_ALREADY_EXIST);
		}
		
		User user = AuthMappingHelper.toUser(request);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user = userRepository.save(user);
		if (user.getId() != null) {
			return IResponseConstants.REGISTRATION_SUCCESS;
		} else {
			throw new AuthException(IExceptionConstants.REGISTRATION_FAILED);
		}
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		String email = request.getEmail();
		String password = request.getPassword();
		
		if (userRepository.existsByEmail(email)) {
			User user = userRepository.findByEmail(email).get();
			if (passwordEncoder.matches(password, user.getPassword())) {
				String token = jwtUtil.generateToke(user.getEmail());
				return new LoginResponse(token, ProjectMappingHelper.toProjectMemberResponse(user));
			}
		}
		throw new AuthException(IExceptionConstants.LOGIN_FAILED);
	}
	
}
