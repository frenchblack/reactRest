package com.spring.react.service.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.manage.ManageCategoryMapper;
import com.spring.react.vo.manage.ManageCategoryVO;

@Service
public class ManageCategoryService {
	@Autowired
	public ManageCategoryMapper mapper;
    @Autowired
    private AdminLogService adminLogService;
    
    public List<ManageCategoryVO> GetManageCategoryList() {
        return mapper.SelectManageCategoryList();
    }
}
