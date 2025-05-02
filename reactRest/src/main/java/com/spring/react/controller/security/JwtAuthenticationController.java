package com.spring.react.controller.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.config.JwtTokenUtil;
import com.spring.react.service.security.JwtUserDetailsService;
import com.spring.react.vo.UserVO;
import com.spring.react.vo.security.JwtResponse;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody UserVO authenticationRequest) throws Exception {
		//인증
		authenticate(authenticationRequest.getUser_id(), authenticationRequest.getUser_pw());

		return ResponseEntity.ok(userDetailsService.createToken(authenticationRequest));
	}
	
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 필터에서 이미 처리한 응답을 전달하는 역할
        // 이 컨트롤러 자체는 로직을 수행하지 않고 필터가 응답을 작성하도록 위임

        return ResponseEntity.ok().build(); 
    }
	
	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
