package com.spring.react.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
public class BoardVO {
    public int board_no;
    public String title;
    public String content;
    public String writer;
    public String user_nm;
    public String write_date;
    public int like_cnt;
    public int view_cnt;
    public boolean ex_del;
    public boolean ex_image;
    public boolean ex_file;

    // ✅ 썸네일
    public String thumb_url;

    public String p_category_cd;
    public String category_cd;
    public String category_nm;
    public String menu_cd;
    public String uuid;
}
