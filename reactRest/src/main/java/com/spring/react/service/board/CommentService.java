package com.spring.react.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.board.CommentMapper;
import com.spring.react.vo.CommentVO;

@Service
public class CommentService {
	@Autowired
	public CommentMapper mapper;

	public List<CommentVO> getCommentList(int board_no) {
		return mapper.getCommentList(board_no);
	}
}
