package com.ps.helper;

import com.ps.dto.request.RegisterRequest;
import com.ps.entity.User;

public interface AuthMappingHelper {

	public static User toUser(RegisterRequest registerRequest) {
		User user = new User();
		user.setName(registerRequest.getName());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(registerRequest.getPassword());
		return user;
	}
}
