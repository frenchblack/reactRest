package com.spring.react.service.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.board.BoardMapper;
import com.spring.react.vo.BoardVO;

@Service
public class BoardService {
	@Autowired
	public BoardMapper mapper;
	
	public Map<String, Object> getBoardList(int page,int maxNext, int size, String keyword, String type, String category, String subCategory, String menu_cd, String sort, String period) {
		int offset = page * size;
		List<BoardVO> list = mapper.getBoardList(offset, size, keyword, type, category , subCategory, menu_cd, sort, period); 

	    int nextCount = 0;
	    for (int i = 1; i <= maxNext; i++) {
	        int nextOffset = (page + i) * size;
	        List<BoardVO> temp = mapper.getBoardList(nextOffset, size, keyword, type, category , subCategory, menu_cd, sort, period);
	        if (temp == null || temp.isEmpty()) break;
	        nextCount++;
	    }
		
	    Map<String, Object> result = new HashMap<>();
	    result.put("content", list);
	    result.put("nextCount", nextCount);
	    return result;
	}

	public int postBoard(BoardVO boardVo) {
		mapper.postBoard(boardVo);
	    return boardVo.getBoard_no(); 
	}
}
