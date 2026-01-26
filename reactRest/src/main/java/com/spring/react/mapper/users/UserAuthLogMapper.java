package com.spring.react.mapper.users;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.users.UserAuthLogVO;

@Repository
@Mapper
public interface UserAuthLogMapper {
    int insertUserAuthLog(UserAuthLogVO user_auth_log_vo);
}