package com.spring.react.controller.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.containers.HeaderService;
import com.spring.react.service.manage.ManageService;
import com.spring.react.vo.MenuVO;

@RestController
//@RequestMapping("containers")
public class Manage {
	@Autowired
	public ManageService manageService;

	@GetMapping("/getManageMenuList")
	public List<MenuVO> getManageMenuList() {
		System.out.println("getManageMenuList");
		return manageService.getManageMenuList();
	}
}
