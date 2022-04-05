package com.spring.react.mapper.users;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.UserVO;

@Repository
@Mapper
public interface UsersMapper {
	UserVO getUserName(String user_nm);
	int join(UserVO userVO);
	UserVO authenticate(String user_id, String user_pw);
}
