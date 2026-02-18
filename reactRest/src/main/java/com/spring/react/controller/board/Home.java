package com.spring.react.controller.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.HomeService;
import com.spring.react.vo.home.HomeBestBoardVO;
import com.spring.react.vo.home.HomeHotMenuVO;
import com.spring.react.vo.home.HomeNewsVO;

@RestController
public class Home {

    @Autowired
    private HomeService homeService;

    @GetMapping("/getHomeNews")
    public List<HomeNewsVO> getHomeNews(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return homeService.getHomeNews(limit);
    }

    @GetMapping("/getHomeHotMenus")
    public List<HomeHotMenuVO> getHomeHotMenus(
              @RequestParam(defaultValue = "14") int days
            , @RequestParam(defaultValue = "8") int limit
    ) {
        return homeService.getHomeHotMenus(days, limit);
    }

    @GetMapping("/getHomeBestBoards")
    public List<HomeBestBoardVO> getHomeBestBoards(
              @RequestParam String menu_cd
            , @RequestParam(defaultValue = "LIKE") String sort_cd
            , @RequestParam(defaultValue = "10") int limit
    ) {
        return homeService.getHomeBestBoards(menu_cd, sort_cd, limit);
    }
}
