package com.spring.react.controller.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.CommentService;
import com.spring.react.vo.CommentListResponseVO;
import com.spring.react.vo.UserVO;

@RestController
public class Comment {
	@Autowired
	public CommentService commentService;
	
	@GetMapping("/getCommentList")
    CommentListResponseVO getCommentList(
          @RequestParam int board_no
        , @RequestParam(required = false, defaultValue = "1") int page
        , @RequestParam(required = false, defaultValue = "10") int size
        , @AuthenticationPrincipal UserVO user
    ) {

        //현재 토큰 작성자 명
        String viewer_user_id = (user == null) ? null : user.getUsername();;

        return commentService.getCommentList(board_no, page, size, viewer_user_id);
    }
	
}
