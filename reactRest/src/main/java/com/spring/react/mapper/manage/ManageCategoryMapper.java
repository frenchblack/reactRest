package com.spring.react.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.react.vo.manage.ManageCategoryVO;

@Mapper
public interface ManageCategoryMapper {

	List<ManageCategoryVO> SelectManageCategoryList();
}
