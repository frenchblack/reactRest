package com.spring.react.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.board.CommentMapper;
import com.spring.react.service.common.OwnershipService;
import com.spring.react.vo.CommentListResponseVO;
import com.spring.react.vo.CommentPagingVO;
import com.spring.react.vo.CommentVO;

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
	
	//코멘트 작성자와 토큰의 유저가 같은 유저인지 확인.
	public boolean isWriter(int comment_no, String user_id) {
		return ownershipService.isCommentOwner(comment_no, user_id);
	}
}
