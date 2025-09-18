package com.spring.react.controller.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.CommentService;
import com.spring.react.vo.CommentVO;

@RestController
public class Comment {
	@Autowired
	public CommentService commentService;
	
	@GetMapping("/getCommentList")
	List<CommentVO> getCommentList(@RequestParam int board_no) {
		return commentService.getCommentList(board_no);
	}
	
}
