package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ps.constant.IExceptionConstants;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.RegisterRequest;
import com.ps.dto.response.LoginResponse;
import com.ps.dto.response.ProjectMemberResponse;
import com.ps.entity.User;
import com.ps.exception.AuthException;
import com.ps.repo.UserRepository;
import com.ps.util.JwtUtil;
import com.ps.util.TestDataUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private JwtUtil jwtUtil;

	@Test
	void testRegister() {
		RegisterRequest request = getRegisterRequest();
		User user = TestDataUtil.getUser();
		
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$KlWP5pWq/6P39oc5qEcUZOA0X3E.gsc.Xsjv/TUG/HnvTBmu0hDBy");
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		// successfully registered
		String message = authService.register(request);
		assertEquals(IResponseConstants.REGISTRATION_SUCCESS, message);

		user.setId(null);
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		// registration failed
		AuthException authException = assertThrows(AuthException.class, () -> authService.register(request));
		assertEquals(IExceptionConstants.REGISTRATION_FAILED, authException.getMessage());
		
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
		
		// user already exists
		authException = assertThrows(AuthException.class, () -> authService.register(request));
		assertEquals(IExceptionConstants.USER_ALREADY_EXIST, authException.getMessage());
	}
	
	@Test
	void testLogin() {
		LoginRequest request = getLoginRequest();
		User user = TestDataUtil.getUser();
		Optional<User> optional = Optional.of(user);
		String token = TestDataUtil.getJwtToken();
		
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
		when(userRepository.findByEmail(request.getEmail())).thenReturn(optional);
		when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
		when(jwtUtil.generateToken(request.getEmail())).thenReturn(token);

		// successful login
		LoginResponse response = authService.login(request);
		ProjectMemberResponse member = response.getUser();
		assertEquals(token, response.getToken());
		assertEquals(user.getId(), member.getId());
		assertEquals(user.getEmail(), member.getEmail());
		
		when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);
		
		// wrong password
		AuthException authException = assertThrows(AuthException.class, () -> authService.login(request));
		assertEquals(IExceptionConstants.WRONG_PASSWORD, authException.getMessage());
		
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
		
		// user not exist with given email
		authException = assertThrows(AuthException.class, () -> authService.login(request));
		assertEquals(IExceptionConstants.LOGIN_FAILED, authException.getMessage());
	}
	
	private RegisterRequest getRegisterRequest() {
		RegisterRequest request = RegisterRequest.builder()
				.email(TestDataUtil.getUserEmail())
				.name("Ro Sh")
				.password("123456")
				.build();
		return request;
	}
	
	private LoginRequest getLoginRequest() {
		LoginRequest request = LoginRequest.builder()
				.email(TestDataUtil.getUserEmail())
				.password("123456")
				.build();
		return request;
	}

}
