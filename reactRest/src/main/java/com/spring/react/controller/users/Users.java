package com.spring.react.controller.users;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.security.JwtUserDetailsService;
import com.spring.react.vo.UserVO;

@RestController
//@RequestMapping("containers")
public class Users {

	@Autowired
	public JwtUserDetailsService jwtUserDetailsService;

	//회원가입
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public Map<String, Object> join(@RequestBody UserVO userVO) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("result", jwtUserDetailsService.join(userVO));
		map.put("message", "회원가입이 완료 되었습니다.");
		
		return map;
	}
	
	//중복확인
	@RequestMapping(value = "/checkId", method = RequestMethod.POST)
	public Map<String, Object> checkId(@RequestBody UserVO userVO) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("result", jwtUserDetailsService.checkId(userVO.getUsername()) == null ? "0" : "-1");
		map.put("message", "");
		
		return map;
	}
	
	@RequestMapping(value = "/getUserMenu", method = RequestMethod.GET)
	public Map<String, Object> getUserMenu(@RequestParam String user_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("message", "asdasdasd");
		
		return map;
	}
}
