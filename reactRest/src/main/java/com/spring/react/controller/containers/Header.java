package com.spring.react.controller.containers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.containers.HeaderService;
import com.spring.react.service.manage.ManageService;
import com.spring.react.vo.MenuVO;

@RestController
public class Header {

	@Autowired
	public HeaderService headerService;

	@GetMapping("/getMenuList")
	public List<MenuVO> getMenuList() {
		return headerService.getMenuList();
	}
}
