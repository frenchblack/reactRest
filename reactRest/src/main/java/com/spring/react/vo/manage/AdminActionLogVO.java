package com.spring.react.vo.manage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminActionLogVO {
    private long log_id;

    private String user_id;
    private String action_cd;
    private String target_cd;

    private String req_uri;
    private String ip_addr;
    private String user_agent;

    private String success_yn;  // 'Y'/'N'
    private String error_msg;

    private String req_json;
}
