package com.spring.react.service.manage;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.react.mapper.manage.AdminLogMapper;
import com.spring.react.vo.manage.AdminActionLogVO;
import com.spring.react.vo.users.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminLogService {

    private static final Logger log = LoggerFactory.getLogger(AdminLogService.class);

    private final AdminLogMapper adminLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void write(HttpServletRequest request
                    , String action_cd
                    , String target_cd
                    , String success_yn
                    , String error_msg
                    , Object req_body_obj) {

        String user_id = null;

        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserVO) {
                user_id = ((UserVO) principal).getUser_id();
            } else {
                user_id = String.valueOf(principal);
            }
        } catch (Exception e) {
            user_id = "UNKNOWN";
        }

        String req_uri = request != null ? request.getRequestURI() : null;
        String ip_addr = request != null ? request.getRemoteAddr() : null;
        String user_agent = request != null ? request.getHeader("User-Agent") : null;

        String req_json = null;
        try {
            if (req_body_obj != null) {
                req_json = objectMapper.writeValueAsString(req_body_obj);
            }
        } catch (Exception e) {
            req_json = null;
        }

        // ✅ 콘솔 로그
        if ("Y".equals(success_yn)) {
            log.info("ADMIN_ACTION OK user_id={} action_cd={} target_cd={} uri={}"
                   , user_id, action_cd, target_cd, req_uri);
        } else {
            log.warn("ADMIN_ACTION FAIL user_id={} action_cd={} target_cd={} uri={} err={}"
                   , user_id, action_cd, target_cd, req_uri, error_msg);
        }

        // ✅ DB 로그
        AdminActionLogVO vo = new AdminActionLogVO();
        vo.setUser_id(user_id);
        vo.setAction_cd(action_cd);
        vo.setTarget_cd(target_cd);
        vo.setReq_uri(req_uri);
        vo.setIp_addr(ip_addr);
        vo.setUser_agent(user_agent);
        vo.setSuccess_yn(success_yn);
        vo.setError_msg(error_msg);
        vo.setReq_json(req_json);

        adminLogMapper.insertAdminActionLog(vo);
    }
}
