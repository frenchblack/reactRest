package com.spring.react.controller.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spring.react.service.manage.ManageCategoryService;
import com.spring.react.vo.manage.ManageCategoryVO;

@RestController
@RequestMapping("/manage/category")
public class ManageCategory {

    @Autowired
    private ManageCategoryService categoryService;

    @GetMapping("/manageList")
    public List<ManageCategoryVO> manageList() {
        return categoryService.getManageCategoryList();
    }

    @PostMapping("/create")
    public Map<String, Object> create(HttpServletRequest request, @RequestBody ManageCategoryVO vo) {
        categoryService.createCategory(request, vo);

        Map<String, Object> res = new HashMap<>();
        res.put("result", "OK");
        return res;
    }

    @PutMapping("/update")
    public Map<String, Object> update(HttpServletRequest request, @RequestBody ManageCategoryVO vo) {
        categoryService.updateCategory(request, vo);

        Map<String, Object> res = new HashMap<>();
        res.put("result", "OK");
        return res;
    }

    @DeleteMapping("/delete")
    public Map<String, Object> delete(HttpServletRequest request, @RequestParam("category_cd") String category_cd) {
        categoryService.deleteCategory(request, category_cd);

        Map<String, Object> res = new HashMap<>();
        res.put("result", "OK");
        return res;
    }
}
