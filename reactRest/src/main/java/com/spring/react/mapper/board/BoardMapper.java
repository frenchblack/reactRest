package com.spring.react.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.BoardVO;

@Repository
@Mapper
public interface BoardMapper {
	List<BoardVO> getBoardList(int offset, int size, String keyword, String type, String category, String subCategory);
}
