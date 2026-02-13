package com.spring.react.controller.users;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.security.JwtUserDetailsService;
import com.spring.react.service.security.UserAuthLogService;
import com.spring.react.vo.users.UserVO;

@RestController
//@RequestMapping("containers")
public class Users {

	private static final Logger log = LoggerFactory.getLogger(Users.class);

	@Autowired
	public JwtUserDetailsService jwtUserDetailsService;

	@Autowired
    private UserAuthLogService userAuthLogService;

	//회원가입
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public Map<String, Object> join(@RequestBody UserVO userVO) {
		Map<String, Object> map = new HashMap<>();

		map.put("result", jwtUserDetailsService.join(userVO));
		map.put("message", "회원가입이 완료 되었습니다.");

		return map;
	}

	//중복확인
	@RequestMapping(value = "/checkId", method = RequestMethod.POST)
	public Map<String, Object> checkId(@RequestBody UserVO userVO) {
		Map<String, Object> map = new HashMap<>();

		map.put("result", jwtUserDetailsService.checkId(userVO.getUsername()) == null ? "0" : "-1");
		map.put("message", "");

		return map;
	}

	//로그아웃
    @RequestMapping(value = "/userLogout", method = RequestMethod.POST)
    public Map<String, Object> logout(HttpServletRequest request, @RequestBody UserVO userVO) {

        Map<String, Object> map = new HashMap<>();

        String user_id = userVO.getUsername();
        int result = jwtUserDetailsService.initRefreshtoken(user_id);

        // ✅ 시스템 로그
        log.info("LOGOUT user_id={} result={}", user_id, result);

        // ✅ DB 로그 (일단 성공 로그로만)
        userAuthLogService.writeLog(request, user_id, "LOGOUT", "Y", null);

        map.put("result", result);
        map.put("message", "로그아웃이 완료되었습니다.");

        return map;
    }


	@RequestMapping(value = "/getUserMenu", method = RequestMethod.GET)
	public Map<String, Object> getUserMenu(@RequestParam String user_id) {
		Map<String, Object> map = new HashMap<>();

		map.put("message", "asdasdasd");

		return map;
	}
}
