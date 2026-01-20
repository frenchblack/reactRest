package com.spring.react.vo;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CommentPagingVO {
    public int page;
    public int size;
    public int total_cnt;
    public int total_page;
}