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
	
	public Map<String, Object> getBoardList(int page,int maxNext, int size, String keyword, String type) {
		int offset = page * size;
		List<BoardVO> list = mapper.getBoardList(offset, size, keyword, type); 

	    int nextCount = 0;
	    for (int i = 1; i <= maxNext; i++) {
	        int nextOffset = (page + i) * size;
	        List<BoardVO> temp = mapper.getBoardList(nextOffset, size, keyword, type);
	        if (temp == null || temp.isEmpty()) break;
	        nextCount++;
	    }
		
	    Map<String, Object> result = new HashMap<>();
	    result.put("content", list);
	    result.put("nextCount", nextCount);
	    return result;
	}
}
