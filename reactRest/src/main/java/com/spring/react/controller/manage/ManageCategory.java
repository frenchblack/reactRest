package com.spring.react.controller.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.manage.ManageCategoryService;
import com.spring.react.vo.manage.ManageCategoryVO;

@RestController
@RequestMapping("/manage/category")
public class ManageCategory {
	@Autowired
	public ManageCategoryService categoryService;

    @GetMapping("/manageList")
    public List<ManageCategoryVO> GetManageCategoryList() {
        return categoryService.GetManageCategoryList();
    }
}
