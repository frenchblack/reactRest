package com.spring.react.controller.util;

import com.spring.react.vo.UserVO;

public class AuthUtil {
    public static String requireUserId(UserVO user) {
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        return user.getUsername();
    }
}
