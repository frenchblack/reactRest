package com.spring.react.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.board.BoardMapper;
import com.spring.react.mapper.board.CommentMapper;

@Service
public class OwnershipService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private CommentMapper commentMapper;

    // 게시글 작성자 여부
    public boolean isBoardOwner(int board_no, String user_id) {
    	if (user_id == null) {
			return false;
		}
    	String writer = boardMapper.getWriter(board_no);
		boolean result = false;
		if (writer == null) {
			return result;
		}

		if( writer != null && user_id != null && writer.equals(user_id)) {
			result = true;
		}

		return result;
    }

    // 댓글 작성자 여부
    public boolean isCommentOwner(int comment_no, String user_id) {
        if (user_id == null) {
			return false;
		}

        return commentMapper.getWriter(comment_no, user_id) > 0;
    }
}