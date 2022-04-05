package com.spring.react.mapper.containers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.MenuVO;

@Repository
@Mapper
public interface HeaderMapper {
	List<MenuVO> getMenuList();
}
