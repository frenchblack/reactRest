package com.spring.react.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.react.service.security.JwtUserDetailsService;
import com.spring.react.vo.security.JwtResponse;
import com.spring.react.vo.users.UserVO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // =========================================================
        // [0] Authorization 헤더에서 Access Token(Bearer ...) 읽기
        // =========================================================
        final String requestTokenHeader = request.getHeader("Authorization");

        // ※ 요청마다 Authorization 전체를 찍으면 로그 도배됨
        //    필요하면 DEBUG로만 확인 (토큰 전체는 가급적 안 찍는게 좋음)
        // log.debug("Authorization header: {}", requestTokenHeader);

        String username = null;  // access token에서 추출한 사용자ID
        String jwtToken = null;  // "Bearer " 제거한 실제 토큰 문자열
        int status = HttpStatus.UNAUTHORIZED.value(); // 기본 인증 실패 코드(401)

        // =========================================================
        // [1] Authorization 헤더가 정상 Bearer 형식인 경우
        //  - Bearer {token}
        //  - Bearer undefined/null 같은 이상값은 제외
        // =========================================================
        if ((requestTokenHeader != null && !requestTokenHeader.equals(""))
                && requestTokenHeader.startsWith("Bearer ")
                && !"Bearer undefined".equals(requestTokenHeader)
                && !"Bearer null".equals(requestTokenHeader)) {

            // "Bearer " 제거
            jwtToken = requestTokenHeader.substring(7);

            try {
                // =====================================================
                // [1-1] Access Token 파싱 성공: username 추출
                //  - 정상 토큰이면 여기서 username이 채워짐
                // =====================================================
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            } catch (IllegalArgumentException e) {
                // =====================================================
                // [1-2] 토큰 파싱 자체가 불가능(형식 오류 등)
                //  - 보통 재로그인 유도 케이스
                // =====================================================
                log.warn("JWT parse failed (IllegalArgumentException). path={}", request.getRequestURI());
                throw e;

            } catch (ExpiredJwtException e) {
                // =====================================================
                // [1-3] Access Token 만료(Expired)
                //
                // 현재 구조:
                //  - 이 필터에서 request body(JSON)로 Refresh/User를 읽어서
                //  - Refresh 검증 성공하면 여기서 바로 새 토큰을 response로 내려줌
                //  - Refresh 정보가 없으면 433 내려서 클라 인터셉터가 /refresh 호출하도록 유도
                // =====================================================
                log.info("Access token expired. path={}", request.getRequestURI());

                String requestRefreshtoken = null; // 요청 바디의 "Refresh"
                String requestUser = null;         // 요청 바디의 "User"

                // -----------------------------------------------------
                // [1-3-1] 요청 body에서 Refresh/User 읽기 시도
                //  - body가 없거나 JSON이 아니면 예외 → 아래 catch로 넘어감
                // -----------------------------------------------------
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> map = mapper.readValue(request.getReader(), HashMap.class);

                    requestRefreshtoken = map.get("Refresh");
                    requestUser = map.get("User");

                } catch (Exception read_e) {
                    // body 파싱 실패: refresh 정보를 못 얻음
                    // (GET 요청 등 body 없는 요청이면 흔히 여기로 떨어짐)
                    log.debug("Failed to read refresh info from request body. path={}", request.getRequestURI());
                }

                // -----------------------------------------------------
                // [1-3-2] Refresh 정보 존재 여부에 따른 분기
                // -----------------------------------------------------
                if (requestRefreshtoken != null && !requestRefreshtoken.equals("")) {

                    // Refresh도 "Bearer "로 시작하고, User 값이 있어야 검증 진행
                    if (requestRefreshtoken.startsWith("Bearer ") && requestUser != null) {

                        String r_token = requestRefreshtoken.substring(7);
                        boolean isValidate = false;

                        // ---------------------------------------------
                        // [1-3-3] Refresh 토큰 검증(DB의 refresh와 비교)
                        // ---------------------------------------------
                        try {
                            isValidate = validateRefresh(requestUser, r_token);
                        } catch (Exception r_e) {
                            // 검증 과정 오류 → isValidate=false 유지
                            log.warn("Refresh validate error. user_id={} path={}", requestUser, request.getRequestURI());
                        }

                        // ---------------------------------------------
                        // [1-3-4] Refresh 검증 성공 → 새 토큰 발급 응답
                        // ---------------------------------------------
                        if (isValidate) {
                            log.info("Refresh token valid. issue new tokens. user_id={}", requestUser);

                            response.setContentType("application/json");
                            response.setCharacterEncoding("utf-8");

                            PrintWriter writer = response.getWriter();
                            ObjectMapper objMapper = new ObjectMapper();

                            writer.write(objMapper.writeValueAsString(doRefreshTokens(requestUser)));

                            // NOTE: 여기서 status 변수만 200으로 바꾸지만,
                            // response.setStatus(200)은 호출하지 않음(원래 코드 그대로 유지)
                            status = HttpStatus.OK.value();

                        } else {
                            // -----------------------------------------
                            // [1-3-5] Refresh 검증 실패 → 401
                            // -----------------------------------------
                            log.warn("Refresh token invalid. user_id={}", requestUser);

                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(status); // 401
                        }

                    } else {
                        // ---------------------------------------------
                        // [1-3-6] Refresh 형식이 아니거나 User가 없음 → 401
                        // ---------------------------------------------
                        log.warn("Refresh header/user invalid. path={}", request.getRequestURI());

                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setStatus(status); // 401
                    }

                } else {
                    // -------------------------------------------------
                    // [1-3-7] Refresh 정보 자체가 없음 → 433
                    //  - 클라 인터셉터가 433을 보고 /refresh 호출하도록 만드는 신호
                    // -------------------------------------------------
                    log.info("Refresh info not found. return 433. path={}", request.getRequestURI());

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(433);
                }

            } catch (SignatureException e) {
                // =====================================================
                // [1-4] 토큰 서명(Signature) 오류 → 위변조/잘못된 토큰
                // =====================================================
                log.warn("JWT signature invalid. path={}", request.getRequestURI());
                // 원래 코드처럼 throw는 하지 않고 통과(동작 유지)
            }

        } else if ((requestTokenHeader != null && !requestTokenHeader.equals(""))) {
            // =========================================================
            // [2] Authorization 헤더는 있는데 "Bearer "로 시작하지 않음
            // =========================================================
            logger.warn("JWT Token does not begin with Bearer String");
            log.warn("Authorization header does not begin with Bearer. path={}", request.getRequestURI());
        }

        // =========================================================
        // [3] username이 있고, 아직 인증(Authentication)이 없으면
        //     → UserDetails 로드 → 토큰 검증 → SecurityContext에 인증 세팅
        // =========================================================
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // DB에서 사용자 정보(+권한 포함) 로딩
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // 토큰 유효성 검사(만료/서명/클레임 등)
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Spring Security 인증 등록
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                // 너무 시끄러우면 DEBUG 권장
                log.debug("Authentication set for user_id={} path={}", username, request.getRequestURI());
            }
        }

        // =========================================================
        // [4] 다음 필터/컨트롤러로 진행
        // =========================================================
        chain.doFilter(request, response);
    }

    // =============================================================
    // Refresh 토큰 유효성 검사
    //  - DB에 저장된 refresh 토큰과 요청 refresh 토큰을 비교/검증
    // =============================================================
    private boolean validateRefresh(String username, String r_token) {
        String user_r_token = jwtUserDetailsService.getRefreshtoken(username);
        return jwtTokenUtil.validateRefreshtoken(r_token, user_r_token);
    }

    // =============================================================
    // 토큰 재발급
    //  - user_id만 세팅해서 createToken 호출
    // =============================================================
    private JwtResponse doRefreshTokens(String username) {
        UserVO userVO = new UserVO();
        userVO.setUser_id(username);

        return jwtUserDetailsService.createToken(userVO);
    }
}
