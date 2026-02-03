package com.spring.react.vo.manage;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ManageCategoryVO {

    private String category_cd;        // MENU_CD or CATEGORY_CD
    private String p_cd;      // 상위 코드 (MENU 또는 CATEGORY)
    private String category_nm;        // MENU_NM or CATEGORY_NM
    private int category_lvl;            // 1~4
    private int sort_order;     // 정렬
    private int use_yn;         // 사용여부(0/1)
    private String menu_cd;     // CATEGORY일 때 소속 메뉴코드(필요하면)
    private String tree_key;
    private String p_tree_key;
}
