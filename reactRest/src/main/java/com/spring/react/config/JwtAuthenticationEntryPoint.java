package com.spring.react.config;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable{
    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
    	Map<String, Object> map = new HashMap<String, Object>();
//    	System.out.println("entryPoint : " + authException);
    	System.out.println("1111" + response.getStatus());
    	if ( authException instanceof UsernameNotFoundException ) {
    		map.put("message", "존재하지 않는 아이디입니다.");
    	} else if ( authException instanceof BadCredentialsException ) {
    		map.put("message", "비밀번호가 일치하지 않습니다.");
    	}
    	
    	if ( !(authException instanceof InsufficientAuthenticationException) ) {
    		System.out.println("if" + response.getStatus());
	    	map.put("code", 401);
	    	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	    	response.setStatus(HttpStatus.UNAUTHORIZED.value());
	    	response.setCharacterEncoding("utf-8");

	        PrintWriter writer = response.getWriter();
	        ObjectMapper objMapper = new ObjectMapper();
	        writer.write(objMapper.writeValueAsString(map));
	        
    	} else if(response.getStatus() == 433){
    		System.out.println("엑세스토큰 만료 433코드 전송");
    	}
    	else{
    		//인증되지 않은 사용자 접근 불가 에러 		
    		//테스트용 출력 삭제필요
    		System.out.println("??" + response.getStatus());
    		
    		System.out.println("테스트" + authException.getCause());
    		System.out.println("테스트" + authException.getClass());
    		System.out.println("JwtAuthenticationEntryPoint : 인증실패에러");
    		map.put("code", 401);
    		map.put("message", "인증된 사용자만 접근 가능합니다");
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    	}
        
    }
}
