package com.spring.react.vo.comment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentReactionRequestVO {
    public int comment_no;
    public int reaction_cd; // 1=LIKE, 0=NONE (나중에 -1 가능)
}
