package com.spring.react.service.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.manage.ManageMapper;
import com.spring.react.vo.MenuVO;
import com.spring.react.vo.UserVO;

@Service
public class ManageService {

	@Autowired
	public ManageMapper mapper;

	public List<MenuVO> getManageMenuList() {
		List<MenuVO> test = mapper.getManageMenuList();
		System.out.println("getManageMenuList" + test);
		
		return test;
		
		//return mapper.getManageMenuList();
	}
	
	public int postMenu(MenuVO menuVO) {
		// TODO Auto-generated method stub
		return mapper.insertMenu(menuVO);
	}

}
