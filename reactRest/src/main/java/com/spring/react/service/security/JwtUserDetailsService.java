package com.spring.react.service.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.users.UsersMapper;
import com.spring.react.vo.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	public UsersMapper mapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userVO = mapper.getUserName(username);
		
		if (userVO != null ) {
			return userVO;
		} else {
			throw new UsernameNotFoundException("user exist : " + username);
		}
	}
	
	//중복확인
	public UserDetails checkId(String username) {
		try {
			return loadUserByUsername(username);
		} catch(UsernameNotFoundException e) {
			return null;
		}
	}
	
	//회원가입
	public int join(UserVO userVO) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		userVO.setUser_pw(encoder.encode(userVO.getPassword()));
	
		
		return mapper.join(userVO);
	}
}
