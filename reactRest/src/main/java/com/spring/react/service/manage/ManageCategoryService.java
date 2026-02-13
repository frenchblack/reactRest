package com.spring.react.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.react.exception.BadRequestException;
import com.spring.react.mapper.manage.ManageCategoryMapper;
import com.spring.react.vo.manage.ManageCategoryVO;

@Service
public class ManageCategoryService {

    @Autowired
    private ManageCategoryMapper mapper;

    @Autowired
    private AdminLogService adminLogService;

    public List<ManageCategoryVO> getManageCategoryList() {
        return mapper.selectManageCategoryList();
    }

    //=======================================================================
    // CREATE (중복확인 + 룰보정 + INSERT) + 관리자 로그
    //=======================================================================
    @Transactional
    public void createCategory(HttpServletRequest request, ManageCategoryVO vo) {

        String action_cd = "CATEGORY_CREATE";
        String target_cd = vo != null ? vo.getCategory_cd() : null;

        try {
            // 1) 기본 검증
            if (vo == null) {
                throw new BadRequestException("REQUEST BODY IS REQUIRED");
            }
            if (vo.getCategory_cd() == null || vo.getCategory_cd().trim().isEmpty()) {
                throw new BadRequestException("CATEGORY_CD IS REQUIRED");
            }
            if (vo.getCategory_nm() == null || vo.getCategory_nm().trim().isEmpty()) {
                throw new BadRequestException("CATEGORY_NM IS REQUIRED");
            }
            if (vo.getCategory_lvl() != 3 && vo.getCategory_lvl() != 4) {
                throw new BadRequestException("ONLY CATEGORY_LVL 3 OR 4 ALLOWED");
            }

            // 2) 코드 중복 체크(서비스 내부)
            int dupCnt = mapper.selectCategoryCdDupCnt(vo.getCategory_cd());
            if (dupCnt > 0) {
                throw new BadRequestException("DUPLICATE CATEGORY_CD");
            }

            // 3) 기본값 보정
            if (vo.getSort_order() <= 0) {
				vo.setSort_order(99);
			}
            if (vo.getUse_yn() != 0 && vo.getUse_yn() != 1) {
				vo.setUse_yn(1);
			}

            // 4) 생성 룰 보정(서버 확정)
            normalizeForCreate(vo);

            // 5) INSERT
            mapper.insertCategory(vo);

            // 6) SUCCESS LOG
            adminLogService.write(request
                                , action_cd
                                , vo.getCategory_cd()
                                , "Y"
                                , null
                                , vo);

        } catch (Exception e) {

            // FAIL LOG
            adminLogService.write(request
                                , action_cd
                                , target_cd
                                , "N"
                                , e.getMessage()
                                , vo);

            throw e;
        }
    }

    //=======================================================================
    // UPDATE + 관리자 로그
    //=======================================================================
    @Transactional
    public void updateCategory(HttpServletRequest request, ManageCategoryVO vo) {

        String action_cd = "CATEGORY_UPDATE";
        String target_cd = vo != null ? vo.getCategory_cd() : null;

        try {
            if (vo == null) {
                throw new BadRequestException("REQUEST BODY IS REQUIRED");
            }

            // 메뉴 노드(lvl 1~2) 수정 금지
            if (vo.getCategory_lvl() <= 2) {
                throw new BadRequestException("MENU NODE CANNOT BE UPDATED");
            }

            if (vo.getCategory_cd() == null || vo.getCategory_cd().trim().isEmpty()) {
                throw new BadRequestException("CATEGORY_CD IS REQUIRED");
            }
            if (vo.getCategory_nm() == null || vo.getCategory_nm().trim().isEmpty()) {
                throw new BadRequestException("CATEGORY_NM IS REQUIRED");
            }
            if (vo.getUse_yn() != 0 && vo.getUse_yn() != 1) {
                throw new BadRequestException("USE_YN MUST BE 0 OR 1");
            }

            mapper.updateCategory(vo);

            // SUCCESS LOG
            adminLogService.write(request
                                , action_cd
                                , vo.getCategory_cd()
                                , "Y"
                                , null
                                , vo);

        } catch (Exception e) {

            // FAIL LOG
            adminLogService.write(request
                                , action_cd
                                , target_cd
                                , "N"
                                , e.getMessage()
                                , vo);

            throw e;
        }
    }

    //=======================================================================
    // DELETE
    //  - 게시글 있으면 DELETE 대신 USE_YN=0 처리
    //  - 관리자 로그: 실제 수행된 동작도 req_json에 남겨줌
    //=======================================================================
    @Transactional
    public void deleteCategory(HttpServletRequest request, String category_cd) {

        String action_cd = "CATEGORY_DELETE";
        String target_cd = category_cd;

        Map<String, Object> req_body_obj = new HashMap<>();
        req_body_obj.put("category_cd", category_cd);

        try {
            if (category_cd == null || category_cd.trim().isEmpty()) {
                throw new BadRequestException("CATEGORY_CD IS REQUIRED");
            }

            int child_cnt = mapper.selectChildCategoryCount(category_cd);

            if (child_cnt > 0) {
                throw new BadRequestException("하위 카테고리가 존재하여 삭제할 수 없습니다.");
            }

            int boardCnt = mapper.selectBoardCntByCategoryCd(category_cd);

            if (boardCnt > 0) {
                mapper.updateCategoryUseYnToZero(category_cd);

                // SUCCESS LOG (실제 동작 기록)
                req_body_obj.put("delete_type", "USE_YN_TO_ZERO");
                req_body_obj.put("board_cnt", boardCnt);

                adminLogService.write(request
                                    , action_cd
                                    , category_cd
                                    , "Y"
                                    , null
                                    , req_body_obj);
                return;
            }

            mapper.deleteCategory(category_cd);

            req_body_obj.put("delete_type", "PHYSICAL_DELETE");
            req_body_obj.put("board_cnt", boardCnt);

            // SUCCESS LOG
            adminLogService.write(request
                                , action_cd
                                , category_cd
                                , "Y"
                                , null
                                , req_body_obj);

        } catch (Exception e) {

            // FAIL LOG
            adminLogService.write(request
                                , action_cd
                                , target_cd
                                , "N"
                                , e.getMessage()
                                , req_body_obj);

            throw e;
        }
    }

    //=======================================================================
    // 내부 룰 보정(CREATE 전용)
    //=======================================================================
    private void normalizeForCreate(ManageCategoryVO vo) {

        // LVL3:
        // - P_CD = NULL
        // - MENU_CD = 상위 메뉴코드 필수
        if (vo.getCategory_lvl() == 3) {
            vo.setP_cd(null);

            if (vo.getMenu_cd() == null || vo.getMenu_cd().trim().isEmpty()) {
                throw new BadRequestException("MENU_CD IS REQUIRED FOR LVL3");
            }
        }

        // LVL4:
        // - MENU_CD = NULL
        // - P_CD = 상위 CATEGORY_CD 필수
        if (vo.getCategory_lvl() == 4) {
            vo.setMenu_cd(null);

            if (vo.getP_cd() == null || vo.getP_cd().trim().isEmpty()) {
                throw new BadRequestException("P_CD IS REQUIRED FOR LVL4");
            }
        }
    }
}
