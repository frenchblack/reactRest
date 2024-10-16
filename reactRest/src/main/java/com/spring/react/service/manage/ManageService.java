package com.spring.react.service.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.manage.ManageMapper;
import com.spring.react.vo.MenuVO;

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
}
