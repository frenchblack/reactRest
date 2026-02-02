package com.spring.react.controller.security;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.spring.react.service.security.JwtUserDetailsService;
import com.spring.react.service.security.UserAuthLogService;
import com.spring.react.vo.users.UserVO;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserAuthLogService userAuthLogService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(HttpServletRequest request, @RequestBody UserVO authenticationRequest) throws Exception {

        String user_id = authenticationRequest.getUser_id();

        try {
            authenticate(authenticationRequest.getUser_id(), authenticationRequest.getUser_pw());

            // ✅ 시스템 로그
            log.info("LOGIN SUCCESS user_id={}", user_id);

            // ✅ DB 로그
            userAuthLogService.writeLog(request, user_id, "LOGIN", "Y", null);
            
            // ✅ 인증 성공 후 DB에서 role 포함 UserDetails 재조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(user_id);

            
            return ResponseEntity.ok(userDetailsService.createToken(userDetails));

        } catch (Exception e) {

            // ✅ 시스템 로그
            log.warn("LOGIN FAIL user_id={} reason={}", user_id, e.getMessage());

            // ✅ DB 로그
            userAuthLogService.writeLog(request, user_id, "LOGIN", "N", e.getMessage());

            throw e;
        }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        // 필터가 응답 작성
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
