package com.spring.react.vo.home;

public class HomeBestBoardVO {

    private int board_no;
    private String title;
    private String write_date;

    private int like_cnt;
    private int view_cnt;

    private String thumb_url;

    private String menu_cd;
    private String menu_nm;
    private String menu_url;

    public int getBoard_no() {
        return board_no;
    }
    public void setBoard_no(int board_no) {
        this.board_no = board_no;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getWrite_date() {
        return write_date;
    }
    public void setWrite_date(String write_date) {
        this.write_date = write_date;
    }
    public int getLike_cnt() {
        return like_cnt;
    }
    public void setLike_cnt(int like_cnt) {
        this.like_cnt = like_cnt;
    }
    public int getView_cnt() {
        return view_cnt;
    }
    public void setView_cnt(int view_cnt) {
        this.view_cnt = view_cnt;
    }
    public String getThumb_url() {
        return thumb_url;
    }
    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }
    public String getMenu_cd() {
        return menu_cd;
    }
    public void setMenu_cd(String menu_cd) {
        this.menu_cd = menu_cd;
    }
    public String getMenu_nm() {
        return menu_nm;
    }
    public void setMenu_nm(String menu_nm) {
        this.menu_nm = menu_nm;
    }
    public String getMenu_url() {
        return menu_url;
    }
    public void setMenu_url(String menu_url) {
        this.menu_url = menu_url;
    }
}
