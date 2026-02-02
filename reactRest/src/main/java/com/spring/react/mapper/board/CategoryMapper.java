package com.spring.react.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.manage.CategoryVO;

@Repository
@Mapper
public interface CategoryMapper {
	List<CategoryVO> getCategories(String menu_cd);
	List<CategoryVO> getSubCategories(String p_cd);
}
