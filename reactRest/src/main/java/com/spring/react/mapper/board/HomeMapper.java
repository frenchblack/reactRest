package com.spring.react.mapper.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.home.HomeBestBoardVO;
import com.spring.react.vo.home.HomeHotMenuVO;
import com.spring.react.vo.home.HomeNewsVO;

@Repository
@Mapper
public interface HomeMapper {

    List<HomeNewsVO> getHomeNews(Map<String, Object> param_map);

    List<HomeHotMenuVO> getHomeHotMenus(Map<String, Object> param_map);

    List<HomeBestBoardVO> getHomeBestBoards(Map<String, Object> param_map);
}
