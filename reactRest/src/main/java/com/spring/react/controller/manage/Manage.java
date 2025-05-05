package com.spring.react.controller.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.containers.HeaderService;
import com.spring.react.service.manage.ManageService;
import com.spring.react.vo.MenuVO;
import com.spring.react.vo.UserVO;

@RestController
//@RequestMapping("containers")
public class Manage {
	@Autowired
	public ManageService manageService;

	@GetMapping("/getManageMenuList")
	public List<MenuVO> getManageMenuList() {
//		System.out.println("getManageMenuList");
		return manageService.getManageMenuList();
	}
	
	@PostMapping("/postMenu")
	public ResponseEntity<?> inserMenu(@RequestBody MenuVO menuVO) {
//		System.out.println("postMenu");
	    int result = manageService.postMenu(menuVO);

	    if (result > 0) {
	        return ResponseEntity.status(HttpStatus.CREATED).body(menuVO);
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	@PutMapping("/putMenu")
	public ResponseEntity<?> updateMenu(@RequestBody MenuVO menuVO) {		
	    int result = manageService.putMenu(menuVO);

	    if (result > 0) {
	        return ResponseEntity.status(HttpStatus.CREATED).body(menuVO);
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	@DeleteMapping("/deleteMenu")
	public ResponseEntity<?> deleteMenu(@RequestBody MenuVO menuVO) {
		System.out.println("deleteMenu" + menuVO);
	    int result = manageService.deleteMenu(menuVO);

	    if (result > 0) {
	        return ResponseEntity.status(HttpStatus.CREATED).body(menuVO);
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
}
