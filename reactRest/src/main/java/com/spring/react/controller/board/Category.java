package com.spring.react.controller.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.CategoryService;
import com.spring.react.vo.manage.CategoryVO;

@RestController
public class Category {
	@Autowired
	public CategoryService categoryService;

	@GetMapping("/getCategories")
	List<CategoryVO> getCategories(@RequestParam String menu_cd) {
		return categoryService.getCategories(menu_cd);
	}

	@GetMapping("/getSubCategories")
	List<CategoryVO> getC_category(@RequestParam String p_cd) {
		return categoryService.getSubCategories(p_cd);
	}
}
