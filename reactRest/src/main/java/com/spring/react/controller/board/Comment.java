package com.spring.react.controller.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.BoardService;
import com.spring.react.service.board.CommentService;
import com.spring.react.vo.UserVO;
import com.spring.react.vo.comment.CommentListResponseVO;
import com.spring.react.vo.comment.CommentReactionRequestVO;
import com.spring.react.vo.comment.CommentReactionResponseVO;
import com.spring.react.vo.comment.CommentUpdateRequestVO;
import com.spring.react.vo.comment.CommentWriteRequestVO;
import com.spring.react.vo.comment.CommentWriteResponseVO;
import com.spring.react.vo.comment.ReplyListResponseVO;

@RestController
public class Comment {

    private final BoardService boardService;
	@Autowired
	public CommentService commentService;

    Comment(BoardService boardService) {
        this.boardService = boardService;
    }
	
	@GetMapping("/getCommentList")
    CommentListResponseVO getCommentList(
          @RequestParam int board_no
        , @RequestParam(required = false, defaultValue = "1") int page
        , @RequestParam(required = false, defaultValue = "10") int size
        , @AuthenticationPrincipal UserVO user
    ) {

        //현재 토큰 작성자 명
        String viewer_user_id = (user == null) ? null : user.getUsername();
        
        return commentService.getCommentList(board_no, page, size, viewer_user_id);
    }

	@PostMapping("/comment/write")
	CommentWriteResponseVO writeComment(
	      @RequestBody CommentWriteRequestVO req
	    , @AuthenticationPrincipal UserVO user
	) {
	    int comment_no = commentService.writeComment(
	          req.getBoard_no()
	        , req.getComment_content()
	        , req.getP_comment_no()
	        , user.getUsername()
	    );

	    CommentWriteResponseVO res = new CommentWriteResponseVO();
	    res.comment_no = comment_no;
	    return res;
	}

	@PostMapping("/comment/update")
	void updateComment(
	      @RequestBody CommentUpdateRequestVO req
	    , @AuthenticationPrincipal UserVO user
	) {
	    //작성자 일치여부 확인.
	    if (!commentService.isWriter(req.getComment_no(), (user == null) ? null : user.getUsername())) {
	    	throw new RuntimeException("작성자가 일치하지 않습니다.");
	    }
        

	    commentService.updateComment(req.getComment_no(), req.getComment_content(), user.getUsername());
	}

	@PostMapping("/comment/delete")
	void deleteComment(
	      @RequestParam int comment_no
	    , @AuthenticationPrincipal UserVO user
	) {
	    //작성자 일치여부 확인.
	    if (!commentService.isWriter(comment_no, (user == null) ? null : user.getUsername())) {
	    	throw new RuntimeException("작성자가 일치하지 않습니다.");
	    }

	    commentService.deleteComment(comment_no, user.getUsername());
	}
	
    @PostMapping("/comment/reaction")
    CommentReactionResponseVO reactComment(
          @RequestBody CommentReactionRequestVO req
        , @AuthenticationPrincipal UserVO user
    ) {
        
        String viewer_user_id = user.getUsername();
        return commentService.reactComment(req.getComment_no(), req.getReaction_cd(), viewer_user_id);
    }
    
    @GetMapping("/getReplyList")
    ReplyListResponseVO getReplyList(
          @RequestParam int board_no
        , @RequestParam int p_comment_no
        , @RequestParam(required = false, defaultValue = "1") int page
        , @RequestParam(required = false, defaultValue = "10") int size
        , @AuthenticationPrincipal UserVO user
    ) {
        String viewer_user_id = (user == null) ? null : user.getUsername();
        return commentService.getReplyList(board_no, p_comment_no, page, size, viewer_user_id);
    }

	
}
