package com.spring.react.controller.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.BoardService;
import com.spring.react.vo.BoardVO;

@RestController
public class Board {
	@Autowired
	public BoardService boardService;
	
	@GetMapping("/getBoardList")
	public List<BoardVO> getBoardList() {
		return boardService.getBoardList();
	}
}
