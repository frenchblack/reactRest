package com.spring.react.service.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.users.UserAuthLogMapper;
import com.spring.react.vo.users.UserAuthLogVO;

@Service
public class UserAuthLogService {

    @Autowired
    private UserAuthLogMapper userAuthLogMapper;

    public void writeLog(HttpServletRequest request,
                         String user_id,
                         String action_type,
                         String success_yn,
                         String fail_reason) {

        UserAuthLogVO vo = new UserAuthLogVO();
        vo.setUser_id(user_id);
        vo.setAction_type(action_type);
        vo.setSuccess_yn(success_yn);
        vo.setFail_reason(fail_reason);

        vo.setIp_addr(getClientIp(request));
        vo.setUser_agent(getUserAgent(request));

        // 로그 INSERT 실패가 본 기능을 망치면 안 되니까 가볍게 보호
        try {
            userAuthLogMapper.insertUserAuthLog(vo);
        } catch (Exception e) {
            // 여기서는 DB 로그 실패로 기능 실패시키지 않음
        }
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private String getClientIp(HttpServletRequest request) {
        // 프록시/로드밸런서 환경 대비(없으면 RemoteAddr)
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.trim().equals("")) {
            // 여러 개면 첫 번째가 원IP인 경우가 많음
            int comma = xff.indexOf(",");
            return (comma > -1) ? xff.substring(0, comma).trim() : xff.trim();
        }
        return request.getRemoteAddr();
    }
}
