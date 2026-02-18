package com.spring.react.service.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.board.HomeMapper;
import com.spring.react.vo.home.HomeBestBoardVO;
import com.spring.react.vo.home.HomeHotMenuVO;
import com.spring.react.vo.home.HomeNewsVO;

@Service
public class HomeService {

    @Autowired
    private HomeMapper homeMapper;

    public List<HomeNewsVO> getHomeNews(int limit) {
        Map<String, Object> param_map = new HashMap<>();
        param_map.put("limit", limit);
        return homeMapper.getHomeNews(param_map);
    }

    public List<HomeHotMenuVO> getHomeHotMenus(int days, int limit) {
        Map<String, Object> param_map = new HashMap<>();
        param_map.put("days", days);
        param_map.put("limit", limit);
        return homeMapper.getHomeHotMenus(param_map);
    }

    public List<HomeBestBoardVO> getHomeBestBoards(String menu_cd, String sort_cd, int limit) {
        Map<String, Object> param_map = new HashMap<>();
        param_map.put("menu_cd", menu_cd);
        param_map.put("sort_cd", sort_cd);
        param_map.put("limit", limit);
        return homeMapper.getHomeBestBoards(param_map);
    }
}
