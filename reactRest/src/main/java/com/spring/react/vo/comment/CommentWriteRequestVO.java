package com.spring.react.vo.comment;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CommentWriteRequestVO {
    private int board_no;
    private String comment_content;
    private Integer p_comment_no;
}
