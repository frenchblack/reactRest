package com.spring.react.service.manage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.react.mapper.manage.ManageMapper;
import com.spring.react.vo.manage.MenuVO;

@Service
public class ManageService {

    @Autowired
    private ManageMapper mapper;

    @Autowired
    private AdminLogService adminLogService;

    // =========================
    // 메뉴 목록 조회 (선택 로그)
    // =========================
    public List<MenuVO> getManageMenuList(HttpServletRequest request) {
        try {
            List<MenuVO> list = mapper.getManageMenuList();

            adminLogService.write(
                request
              , "MENU_READ"
              , null
              , "Y"
              , null
              , null
            );

            return list;

        } catch (Exception e) {
            adminLogService.write(
                request
              , "MENU_READ"
              , null
              , "N"
              , e.getMessage()
              , null
            );
            throw e;
        }
    }

    // =========================
    // 메뉴 생성
    // =========================
    public int postMenu(HttpServletRequest request, MenuVO menuVO) {
        try {
            int result = mapper.insertMenu(menuVO);

            adminLogService.write(
                request
              , "MENU_CREATE"
              , menuVO.getMenu_cd()   // ✅ 수정
              , "Y"
              , null
              , menuVO
            );

            return result;

        } catch (Exception e) {
            adminLogService.write(
                request
              , "MENU_CREATE"
              , menuVO != null ? menuVO.getMenu_cd() : null // ✅ 수정
              , "N"
              , e.getMessage()
              , menuVO
            );
            throw e;
        }
    }

    // =========================
    // 메뉴 수정
    // =========================
    public int putMenu(HttpServletRequest request, MenuVO menuVO) {
        try {
            int result = mapper.updateMenu(menuVO);

            adminLogService.write(
                request
              , "MENU_UPDATE"
              , menuVO.getMenu_cd()   // ✅ 수정
              , "Y"
              , null
              , menuVO
            );

            return result;

        } catch (Exception e) {
            adminLogService.write(
                request
              , "MENU_UPDATE"
              , menuVO != null ? menuVO.getMenu_cd() : null // ✅ 수정
              , "N"
              , e.getMessage()
              , menuVO
            );
            throw e;
        }
    }

    // =========================
    // 메뉴 삭제
    // =========================
    public int deleteMenu(HttpServletRequest request, MenuVO menuVO) {
        try {
            int result = mapper.deleteMenu(menuVO);

            adminLogService.write(
                request
              , "MENU_DELETE"
              , menuVO.getMenu_cd()   // ✅ 수정
              , "Y"
              , null
              , menuVO
            );

            return result;

        } catch (Exception e) {
            adminLogService.write(
                request
              , "MENU_DELETE"
              , menuVO != null ? menuVO.getMenu_cd() : null // ✅ 수정
              , "N"
              , e.getMessage()
              , menuVO
            );
            throw e;
        }
    }
}
