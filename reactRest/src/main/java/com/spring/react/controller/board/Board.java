package com.spring.react.controller.board;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfigurationSource;
import com.spring.react.service.board.BoardService;
import com.spring.react.vo.BoardVO;

@RestController
public class Board {

    private final CorsConfigurationSource corsConfigurationSource;
	@Autowired
	public BoardService boardService;

    Board(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }
	
	@GetMapping("/getBoardList")
	public Map<String, Object> getBoardList(@RequestParam int page
			, @RequestParam(defaultValue = "5") int maxNext
			, @RequestParam(defaultValue = "10") int size
			, @RequestParam(required = false) String keyword
		    , @RequestParam(required = false) String type
		    , @RequestParam(required = false) String category
		    , @RequestParam(required = false) String subCategory
		    , @RequestParam(required = false) String sort
		    , @RequestParam(required = false) String period
		    , String menu_cd) {
	    return boardService.getBoardList(page, maxNext, size, keyword, type, category , subCategory, menu_cd, sort, period);
	}
	
	@PostMapping("/postBoard")
	public int postBoard(@RequestBody BoardVO boardVo) {
		System.out.println("postBoard" + boardVo);
		return boardService.postBoard(boardVo);
	}
	
	@GetMapping("/viewBoard")
	public BoardVO viewBoard(@RequestParam int board_no) {
		BoardVO board_vo = boardService.viewBoard(board_no);
		boardService.increaseViewCnt(board_no);
		
	    return board_vo;
	}
	
}
