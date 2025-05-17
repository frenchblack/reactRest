package com.spring.react.controller.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.BoardService;
import com.spring.react.vo.BoardVO;

@RestController
public class Board {
	@Autowired
	public BoardService boardService;
	
	@GetMapping("/getBoardList")
	public Map<String, Object> getBoardList(@RequestParam int page
			, @RequestParam(defaultValue = "5") int maxNext
			, @RequestParam(defaultValue = "10") int size
			, @RequestParam(required = false) String keyword
		    , @RequestParam(required = false) String type) {
	    return boardService.getBoardList(page, maxNext, size, keyword, type);
	}
}
