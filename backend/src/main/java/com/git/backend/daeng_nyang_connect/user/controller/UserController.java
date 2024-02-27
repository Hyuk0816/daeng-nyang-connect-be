package com.git.backend.daeng_nyang_connect.user.controller;

import com.git.backend.daeng_nyang_connect.oauth.service.OAuthService;
import com.git.backend.daeng_nyang_connect.user.dto.AddExtraInfoDto;
import com.git.backend.daeng_nyang_connect.user.dto.FindDto;
import com.git.backend.daeng_nyang_connect.user.dto.LoginDto;
import com.git.backend.daeng_nyang_connect.user.dto.SignUpDto;
import com.git.backend.daeng_nyang_connect.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
@Tag(name = "유저 API")
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto){return userService.signUp(signUpDto);}

    @Operation(summary = "아이디 중복 체크")
    @GetMapping("/IdCheck")
    public ResponseEntity<?> IdCheck(@RequestParam("Id") String email){
        return userService.checkUserId(email);
    }

    @Operation(summary = "닉네임 중복 체크")
    @GetMapping("/NicknameCheck")
    public ResponseEntity<?> NicknameCheck(@RequestParam("nickname") String nickname){
        return userService.checkUserNickname(nickname);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse){
        return userService.login(loginDto, httpServletResponse);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("access_token") String token){
        userService.logout(token);
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @Operation(summary = "아이디 찾기")
    @GetMapping("/findId")
    public ResponseEntity<?> findUserId(@RequestParam("name") String name,
                                        @RequestParam("mobile") String mobile){
        return userService.findUserId(name,mobile);
    }

    @Operation(summary = "비밀번호 찾기")
    @PostMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody FindDto findDto){
        return userService.setNewPassword(findDto);
    }

    @Operation(summary = "유저 삭제")
   @DeleteMapping("/deleteUser")
    public ResponseEntity<?>deleteUser(@RequestHeader("access_token") String token,
                                       @RequestBody LoginDto loginDto){
        return userService.deleteUser(token, loginDto);
   }

    @Operation(summary = "네이버 회원 추가 정보 입력")
    @PutMapping("/addNaverInfo")
    private ResponseEntity<?> naverAddInfo(@RequestHeader("access_token") String token,
                                           @RequestBody AddExtraInfoDto addExtraInfoDto){
        return oAuthService.naverAddInfo(token, addExtraInfoDto);
    }

    @Operation(summary = "카카오 회원 추가 정보 입력")
    @PutMapping("/addKakaoInfo")
    private ResponseEntity<?> kakaoAddInfo(@RequestHeader("access_token") String token,
                                           @RequestBody AddExtraInfoDto addExtraInfoDto){
        return oAuthService.kakaoAddInfo(token, addExtraInfoDto);
    }

    @Operation(summary = "리프레쉬 토큰 발급")
    @PostMapping("/refresh")
    public ResponseEntity<?>refresh(@RequestHeader("refresh_token")String token){
        return userService.refresh(token);
    }



}
