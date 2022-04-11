package com.spring.react.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.react.service.security.JwtUserDetailsService;
import com.spring.react.vo.UserVO;
import com.spring.react.vo.security.JwtResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		final String requestRefreshtokenHeader = request.getHeader("Refresh");
		final String requestUserHeader = request.getHeader("User");
		
		System.out.println(requestTokenHeader);
		
		String username = null;
		String jwtToken = null;

		// Authorization에 토큰이 존재하며 Bearer로 시작 시
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			// "Bearer " 단어 삭제
			jwtToken = requestTokenHeader.substring(7);
			try {
				//토큰으로부터 유저 닉네임 가져옴
//				jwtTokenUtil.isTokenExpired(jwtToken);
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			//잘못된 액세스 토큰
			} catch (IllegalArgumentException e) {
				//재로그인요청 보내야함.
				System.out.println("Unable to get JWT Token");
				throw e;
			//액세스토큰 만료
			} catch (ExpiredJwtException e) {
				//리프레시토큰 검사
				if ( requestRefreshtokenHeader !=  null && !requestRefreshtokenHeader.equals("") ) {
					if ( requestRefreshtokenHeader.startsWith("Bearer ") && requestUserHeader != null ) {
						String r_token = requestRefreshtokenHeader.substring(7);
						boolean isValidate = false;
						try {
							System.out.println("start refresh validate");
							isValidate = validateRefresh(requestUserHeader, r_token);
							System.out.println("end refresh validate : " + isValidate);
						} catch (Exception r_e) {
							System.out.println("error refresh validate : " + r_e);
							// TODO: handle exception
						}
						
						if ( isValidate ) {
							PrintWriter writer = response.getWriter();
							ObjectMapper objMapper = new ObjectMapper();
							
							response.setContentType("application/json");
					        response.setCharacterEncoding("utf-8");
							
							writer.write(objMapper.writeValueAsString(doRefreshTokens(requestUserHeader)));
							
//							String result = objMapper.writeValueAsString(doRefreshTokens(requestUserHeader));
//									ResponseEntity.ok(doRefreshTokens(requestUserHeader)).toString();
							
							
							
							System.out.println("doRefreshTokens");
							
						}
					} else {
						throw new IllegalArgumentException();
					}
				} else {
					System.out.println("not eixist refresh token");
				}
//				System.out.println("JWT Token has expired"  + requestRefreshtokenHeader);
//				throw e;
			} catch (SignatureException e) {
				//잘못된 토큰
				System.out.println("SignatureException");
				throw e;
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
			System.out.println("JWT Token does not begin with Bearer String");
		}
		
		// Once we get the token validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// if token is valid configure Spring Security to manually set
			// authentication
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}
	
	//리프레시토큰 유효성 검사
	private boolean validateRefresh(String username, String r_token) {
		String user_r_token = jwtUserDetailsService.getRefreshtoken(username);
		System.out.println("validateRefresh equals : " + r_token.equals(user_r_token));
		
		return jwtTokenUtil.validateRefreshtoken(r_token, user_r_token);
	}
	
	//토큰재발급
	private JwtResponse doRefreshTokens(String username) {
		UserVO userVO = new UserVO();
		userVO.setUser_id(username);
		
		return jwtUserDetailsService.createToken(userVO);
	}
}
