package com.spring.react.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.MenuVO;

@Repository
@Mapper
public interface ManageMapper {
	List<MenuVO> getManageMenuList();
	int insertMenu(MenuVO menuVO);
	int updateMenu(MenuVO menuVO);
	int deleteMenu(MenuVO menuVO);
}
