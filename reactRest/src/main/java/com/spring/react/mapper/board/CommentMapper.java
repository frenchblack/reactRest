package com.spring.react.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.CommentVO;

@Repository
@Mapper
public interface CommentMapper {
    // 부모댓글 총개수
	int getCommentTotalCnt(@Param("board_no") int board_no);

    // 부모댓글 페이징 리스트
	List<CommentVO> getCommentList(
		      @Param("board_no") int board_no
		    , @Param("size") int size
		    , @Param("offset") int offset
		    , @Param("viewer_user_id") String viewer_user_id
	);
	
    int getWriter(
            @Param("comment_no") int comment_no
          , @Param("user_id") String user_id
      );
}
