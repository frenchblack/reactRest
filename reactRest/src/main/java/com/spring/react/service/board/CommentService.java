package com.spring.react.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.react.mapper.board.CommentMapper;
import com.spring.react.service.common.OwnershipService;
import com.spring.react.vo.comment.CommentListResponseVO;
import com.spring.react.vo.comment.CommentPagingVO;
import com.spring.react.vo.comment.CommentReactionResponseVO;
import com.spring.react.vo.comment.CommentVO;

@Service
public class CommentService {
	@Autowired
	public CommentMapper mapper;
	@Autowired
	public OwnershipService ownershipService;

	public CommentListResponseVO getCommentList(int board_no, int page, int size, String viewer_user_id) {

        if (page <= 0) page = 1;
        if (size <= 0) size = 10;

        int total_cnt = mapper.getCommentTotalCnt(board_no);
        int total_page = (int) Math.ceil((double) total_cnt / (double) size);

        int offset = (page - 1) * size;

        List<CommentVO> list = mapper.getCommentList(board_no, size, offset, viewer_user_id);

        CommentPagingVO paging = new CommentPagingVO();
        paging.page = page;
        paging.size = size;
        paging.total_cnt = total_cnt;
        paging.total_page = total_page;

        CommentListResponseVO res = new CommentListResponseVO();
        res.list = list;
        res.paging = paging;

        return res;
    }
	
	@Transactional
	public int writeComment(int board_no, String comment_content, String user_id) {

	    if (user_id == null || user_id.trim().isEmpty()) {
	        throw new RuntimeException("로그인이 필요합니다.");
	    }

	    if (comment_content == null || comment_content.trim().isEmpty()) {
	        throw new RuntimeException("댓글을 입력하세요.");
	    }

	    CommentVO vo = new CommentVO();
	    vo.board_no = board_no;
	    vo.user_id = user_id;
	    vo.comment_content = comment_content.trim();

	    // ✅ 부모댓글 고정
	    vo.comment_lvl = 0;
	    vo.p_comment_no = 0;

	    // 아래 insert는 "comment_no를 확실히" 돌려받게 구성해야 함
	    int comment_no = mapper.insertComment(vo);
	    return comment_no;
	}

	@Transactional
	public void updateComment(int comment_no, String comment_content, String user_id) {

	    if (user_id == null || user_id.trim().isEmpty()) {
	        throw new RuntimeException("로그인이 필요합니다.");
	    }

	    if (comment_content == null || comment_content.trim().isEmpty()) {
	        throw new RuntimeException("댓글을 입력하세요.");
	    }

	    int upd = mapper.updateCommentContent(comment_no, comment_content.trim(), user_id);
	    if (upd == 0) {
	        throw new RuntimeException("수정 권한이 없거나 삭제된 댓글입니다.");
	    }
	}

	@Transactional
	public void deleteComment(int comment_no, String user_id) {

	    if (user_id == null || user_id.trim().isEmpty()) {
	        throw new RuntimeException("로그인이 필요합니다.");
	    }

	    int upd = mapper.deleteComment(comment_no, user_id);
	    if (upd == 0) {
	        throw new RuntimeException("삭제 권한이 없거나 이미 삭제된 댓글입니다.");
	    }
	}
	
	@Transactional
	public CommentReactionResponseVO reactComment(int comment_no, int reaction_cd, String viewer_user_id) {

		// 0/1/-1 만 허용 (방어)
	    if (reaction_cd != 1 && reaction_cd != 0 && reaction_cd != -1) {
	        throw new RuntimeException("잘못된 reaction_cd 입니다.");
	    }

	    Integer old_cd_obj = mapper.getReactionCd(comment_no, viewer_user_id);
	    int old_cd = (old_cd_obj == null) ? 0 : old_cd_obj;

	    int new_cd = reaction_cd;

	    // LIKE_CNT delta (좋아요만 카운트)
	    int old_like = (old_cd == 1) ? 1 : 0;
	    int new_like = (new_cd == 1) ? 1 : 0;
	    int like_delta = new_like - old_like;

	    // COMMENT_LIKE 반영: new가 0이면 삭제, 아니면 upsert(1 또는 -1)
	    if (new_cd == 0) {
	        mapper.deleteReaction(comment_no, viewer_user_id);
	    } else {
	        mapper.upsertReaction(comment_no, viewer_user_id, new_cd);
	    }

	    // COMMENT like_cnt 반영
	    if (like_delta != 0) {
	        mapper.updateLikeCnt(comment_no, like_delta);
	    }

	    int like_cnt = mapper.getLikeCnt(comment_no);

	    CommentReactionResponseVO res = new CommentReactionResponseVO();
	    res.comment_no = comment_no;
	    res.reaction_cd = new_cd;
	    res.my_like_yn = (new_cd == 1) ? 1 : 0;
	    res.like_cnt = like_cnt;

	    return res;
	}
	
	//코멘트 작성자와 토큰의 유저가 같은 유저인지 확인.
	public boolean isWriter(int comment_no, String user_id) {
		return ownershipService.isCommentOwner(comment_no, user_id);
	}
}
