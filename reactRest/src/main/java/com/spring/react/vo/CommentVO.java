package com.spring.react.vo;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CommentVO {
	public int commment_no;
	public int board_no;
	public String user_id;
	public String user_nm;
	public String user_photo;
	public String write_date;
	public String comment_content;
	public boolean ex_del;
	public int like_cnt;
	public int comment_lvl;
	public String p_comment_no;
	public int c_comment_cnt;
}
