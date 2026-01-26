package com.spring.react.mapper.users;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.users.UserVO;

@Repository
@Mapper
public interface UsersMapper {
	UserVO getUserName(String user_nm);
	int join(UserVO userVO);
	String getRefreshtoken(String user_id);
	int updateRefreshtoken(String user_id, String r_token);
	int initRefreshtoken(String user_id);
}
