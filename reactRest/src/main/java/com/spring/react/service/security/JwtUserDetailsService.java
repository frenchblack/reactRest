package com.spring.react.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.react.config.JwtTokenUtil;
import com.spring.react.mapper.users.UsersMapper;
import com.spring.react.vo.security.JwtResponse;
import com.spring.react.vo.users.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	public UsersMapper mapper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userVO = mapper.getUserName(username);

		if (userVO != null ) {
			return userVO;
		} else {
			throw new UsernameNotFoundException("user exist : " + username);
		}
	}

	//토큰발급
	public JwtResponse createToken(UserDetails userDetails) {
	    UserVO user = (UserVO) userDetails;

	    return new JwtResponse(
	            createAccesstoken(userDetails)
	          , createRefreshtoken(userDetails.getUsername())
	          , user.getRole_cd()
	    );
	}

	//액세스토큰 생성
	private String createAccesstoken(UserDetails userDetails) {
		return jwtTokenUtil.generateToken(userDetails);
	}

	//리프레시토큰 생성
	private String createRefreshtoken(String user_id) {
		String r_token = jwtTokenUtil.generateRefreshtoken();

		mapper.updateRefreshtoken(user_id, r_token);

		return r_token;
	}

	//리프레시 토큰 확인
	public String getRefreshtoken(String user_id) {
		return mapper.getRefreshtoken(user_id);
	}

	//리프레시토큰 초기화
	public int initRefreshtoken(String user_id) {
		return mapper.initRefreshtoken(user_id);
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
