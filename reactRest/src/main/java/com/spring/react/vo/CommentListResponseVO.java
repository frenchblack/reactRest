package com.spring.react.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CommentListResponseVO {
    public List<CommentVO> list;
    public CommentPagingVO paging;
}