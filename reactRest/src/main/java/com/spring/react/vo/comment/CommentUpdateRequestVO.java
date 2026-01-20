package com.spring.react.vo.comment;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CommentUpdateRequestVO {
    private int comment_no;
    private String comment_content;
}