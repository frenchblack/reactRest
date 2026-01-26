package com.spring.react.vo.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuthLogVO {
    private Long log_id;

    private String user_id;
    private String action_type;   // LOGIN | LOGOUT
    private String success_yn;    // Y | N
    private String fail_reason;   // 실패 사유
    private String ip_addr;
    private String user_agent;
}