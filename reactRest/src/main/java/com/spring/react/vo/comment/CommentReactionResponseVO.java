package com.spring.react.vo.comment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentReactionResponseVO {
    public int comment_no;
    public int reaction_cd;
    public int my_like_yn;
    public int like_cnt;
}
