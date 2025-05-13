package com.spring.react.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.board.BoardMapper;
import com.spring.react.vo.BoardVO;

@Service
public class BoardService {
	@Autowired
	public BoardMapper mapper;
	
	public List<BoardVO> getBoardList() {
		return mapper.getBoardList();
	}
}
