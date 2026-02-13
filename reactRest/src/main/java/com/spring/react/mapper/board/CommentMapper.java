package com.spring.react.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.comment.CommentVO;

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

    Integer getReactionCd(
    	      @Param("comment_no") int comment_no
    	    , @Param("user_id") String user_id
    	);

	int upsertReaction(
	      @Param("comment_no") int comment_no
	    , @Param("user_id") String user_id
	    , @Param("reaction_cd") int reaction_cd
	);

	int deleteReaction(
	      @Param("comment_no") int comment_no
	    , @Param("user_id") String user_id
	);

	int updateLikeCnt(
	      @Param("comment_no") int comment_no
	    , @Param("delta") int delta
	);

	int getLikeCnt(@Param("comment_no") int comment_no);

	int insertComment(CommentVO vo);

	int updateCommentContent(
	      @Param("comment_no") int comment_no
	    , @Param("comment_content") String comment_content
	    , @Param("user_id") String user_id
	);

	int deleteComment(
	      @Param("comment_no") int comment_no
	    , @Param("user_id") String user_id
	);

	int getReplyTotalCnt(
		      @Param("board_no") int board_no
		    , @Param("p_comment_no") int p_comment_no
	);

	List<CommentVO> getReplyList(
	      @Param("board_no") int board_no
	    , @Param("p_comment_no") int p_comment_no
	    , @Param("size") int size
	    , @Param("offset") int offset
	    , @Param("viewer_user_id") String viewer_user_id
	);

}
