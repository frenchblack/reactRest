package com.spring.react.controller.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.vo.users.UserVO;

@RestController
public class Auth {

	@GetMapping("/auth/me")
	public Map<String, Object> me(Authentication authentication) {
	    UserVO user = (UserVO) authentication.getPrincipal();

	    Map<String, Object> map = new HashMap<>();
	    map.put("user_id", user.getUser_id());
	    map.put("role_cd", user.getRole_cd());
	    return map;
	}
}
