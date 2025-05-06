package com.spring.react.service.containers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.containers.HeaderMapper;
import com.spring.react.vo.MenuVO;

@Service
public class HeaderService {

	@Autowired
	public HeaderMapper mapper;

	public List<MenuVO> getMenuList() {
		return mapper.getMenuList();
	}

	public List<MenuVO> getChildMenuList() {
		// TODO Auto-generated method stub
		return mapper.getChildMenuList();
	}
}
