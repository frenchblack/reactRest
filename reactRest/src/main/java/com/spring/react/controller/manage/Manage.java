package com.spring.react.controller.manage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.manage.ManageService;
import com.spring.react.vo.manage.MenuVO;

@RestController
//@RequestMapping("containers")
public class Manage {
	@Autowired
	public ManageService manageService;

	@GetMapping("/getManageMenuList")
	public List<MenuVO> getManageMenuList(HttpServletRequest request) {
//		System.out.println("getManageMenuList");
		return manageService.getManageMenuList(request);
	}

	@PostMapping("/postMenu")
	public ResponseEntity<?> inserMenu(HttpServletRequest request, @RequestBody MenuVO menuVO) {
	    int result = manageService.postMenu(request, menuVO);
	    return ResponseEntity.status(HttpStatus.CREATED).body(menuVO);
	}

	@PutMapping("/putMenu")
	public ResponseEntity<?> updateMenu(HttpServletRequest request, @RequestBody MenuVO menuVO) {
	    int result = manageService.putMenu(request, menuVO);
	    return ResponseEntity.status(HttpStatus.CREATED).body(menuVO);
	}

	@DeleteMapping("/deleteMenu")
	public ResponseEntity<?> deleteMenu(HttpServletRequest request, @RequestBody MenuVO menuVO) {
	    int result = manageService.deleteMenu(request, menuVO);
	    return ResponseEntity.status(HttpStatus.CREATED).body(menuVO);
	}
}
