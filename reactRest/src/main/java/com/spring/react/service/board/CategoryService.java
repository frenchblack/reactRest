package com.spring.react.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.board.CategoryMapper;
import com.spring.react.vo.manage.CategoryVO;

@Service
public class CategoryService {
	@Autowired
	public CategoryMapper mapper;

	public List<CategoryVO> getCategories(String menu_cd){
		return mapper.getCategories(menu_cd);
	}
	public List<CategoryVO> getSubCategories(String p_cd){
		return mapper.getSubCategories(p_cd);
	}
}
