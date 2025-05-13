package com.spring.react.vo;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class BoardVO {
	public int board_no;
	public String title;
	public String content;
	public String writer;
	public String write_date;
	public int like_cnt;
	public int view_cnt;
	public boolean is_del;
	public String category_cd;
	public String category_nm;
	public String menu_cd;
}
