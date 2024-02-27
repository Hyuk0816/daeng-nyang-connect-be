package com.git.backend.daeng_nyang_connect.oauth.controller;

import com.git.backend.daeng_nyang_connect.oauth.service.OAuthService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OAuth API")
@RequestMapping("/oauth")
public class OauthController {

    private final OAuthService oAuthService;

    @GetMapping("/naver")
    public ResponseEntity<?> naverLogin(HttpServletRequest request, HttpServletResponse response) {
        return oAuthService.naverLogin(request, response);
    }

    @GetMapping ("/kakao")
    public ResponseEntity<?> kakaoLogin(HttpServletRequest request, HttpServletResponse response) {
        return oAuthService.kakaoLogin(request, response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> oauthLogin(@RequestHeader("access_token")String token,
                                        HttpServletRequest request,
                                        HttpServletResponse response){
        return oAuthService.oauthLogin(token, request, response);
    }

}


