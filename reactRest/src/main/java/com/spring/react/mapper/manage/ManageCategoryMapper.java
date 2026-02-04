package com.spring.react.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.react.vo.manage.ManageCategoryVO;

@Mapper
public interface ManageCategoryMapper {

    List<ManageCategoryVO> selectManageCategoryList();

    int selectCategoryCdDupCnt(@Param("category_cd") String category_cd);

    int selectBoardCntByCategoryCd(@Param("category_cd") String category_cd);

    int insertCategory(ManageCategoryVO vo);

    int updateCategory(ManageCategoryVO vo);

    int updateCategoryUseYnToZero(@Param("category_cd") String category_cd);

    int deleteCategory(@Param("category_cd") String category_cd);
    
    int selectChildCategoryCount(@Param("category_cd") String category_cd);
}
