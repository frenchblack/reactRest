package com.spring.react.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.CommentVO;

@Repository
@Mapper
public interface CommentMapper {
	List<CommentVO> getCommentList(int board_no);
}
