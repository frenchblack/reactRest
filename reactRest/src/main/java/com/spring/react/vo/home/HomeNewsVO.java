package com.spring.react.vo.home;

public class HomeNewsVO {

    private int board_no;
    private String title;
    private String write_date;

    private String type_nm;
    private String menu_cd;
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
    public String getType_nm() {
        return type_nm;
    }
    public void setType_nm(String type_nm) {
        this.type_nm = type_nm;
    }
    public String getMenu_cd() {
        return menu_cd;
    }
    public void setMenu_cd(String menu_cd) {
        this.menu_cd = menu_cd;
    }
    public String getMenu_url() {
        return menu_url;
    }
    public void setMenu_url(String menu_url) {
        this.menu_url = menu_url;
    }
}
