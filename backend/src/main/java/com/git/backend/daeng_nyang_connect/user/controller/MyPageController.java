package com.git.backend.daeng_nyang_connect.user.controller;

import com.git.backend.daeng_nyang_connect.animal.entity.AdoptedAnimal;
import com.git.backend.daeng_nyang_connect.animal.entity.AnimalScrap;
import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDto;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardLikeDto;
import com.git.backend.daeng_nyang_connect.user.dto.ModifyUserDto;
import com.git.backend.daeng_nyang_connect.user.dto.MyBoardDto;
import com.git.backend.daeng_nyang_connect.user.dto.MyPageDto;
import com.git.backend.daeng_nyang_connect.user.dto.MyScrapAnimalResponseDTO;
import com.git.backend.daeng_nyang_connect.user.service.FindMyBoardService;
import com.git.backend.daeng_nyang_connect.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Tag(name = "마이페이지 API")
@RequestMapping("/api/myPage")
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;
    private final FindMyBoardService findMyBoardService;

    @Operation(summary = "마이페이지 조회")
    @GetMapping("/get")
    public MyPageDto getMyPage(@RequestHeader("access_token")String token){
        return myPageService.getMyInfo(token);
    }

    @Operation(summary = "프로필 이미지 변경")
    @PutMapping ("/modifyProfile")
    private Map<String ,String> modifyProfile(@RequestHeader("access_token")String token,
                                              @RequestBody MultipartFile multipartFile) throws FileUploadFailedException {
        return  myPageService.modifyProfile(token, multipartFile);
    }

    @Operation(summary = "유저 소개 변경")
    @PutMapping("/modifyInfo")
    private Map<String ,String> modifyInfo(@RequestHeader("access_token")String token,
                                           @RequestParam("info") String info){
        return myPageService.modifyInfo(token, info);
    }

    @Operation(summary = "닉네임 변경")
    @PutMapping("/modifyNickname")
    public Map<String ,String > modifyNickname(@RequestHeader("access_token")String token,
                                               @RequestParam("nickname") String nickname){
        return myPageService.modifyNickname(token, nickname);
    }

    @Operation(summary = "주소지 변경")
    @PutMapping("/modifyCityTown")
    public ResponseEntity<?> modifyCityTown(@RequestHeader("access_token") String token,
                                            @RequestParam("city") String city,
                                            @RequestParam("town") String town){
        return myPageService.modifyCityTown(token,city,town);
    }

    @Operation(summary = "경험 수정")
    @PutMapping("/modifyExperience")
    public ResponseEntity<?> modifyExperience(@RequestHeader("access_token") String token,
                                            @RequestParam("experience") Boolean experience){
        return myPageService.modifyExperience(token,experience);
    }

    @Operation(summary = "비밀번호 수정")
    @PutMapping("/modifyPassword")
    public Map<String,String > modifyPassword(@RequestHeader("access_token")String token,
                                              @RequestParam("password") String password){
        return myPageService.modifyPassword(token, password);
    }

    @Operation(summary = "모바일 수정")
    @PutMapping("/modifyMobile")
    public ResponseEntity<?> modifyMobile(@RequestHeader("access_token")String token,
                                        @RequestParam("mobile") String mobile){
        return myPageService.modifyMobile(token, mobile);
    }

    @Operation(summary = "이름 수정")
    @PutMapping("/modifyName")
    public ResponseEntity<?> modifyName(@RequestHeader("access_token")String token,
                                        @RequestParam("name") String name){
        return myPageService.modifyName(token, name);
    }

    @Operation(summary = "성별 수정")
    @PutMapping("/modifyGender")
    public ResponseEntity<?> modifyName(@RequestHeader("access_token")String token,
                                        @RequestParam("gender") char gender){
        return myPageService.modifyGender(token, gender);
    }

    @Operation(summary = "내가 쓴 게시물 조회")
    @GetMapping("/getMyBoard")
    public MyBoardDto getMyBoard(@RequestHeader("access_token") String token){
        return findMyBoardService.getMyBoard(token);
    }

    @Operation(summary = "내가 좋아요한 게시물 조회")
    @GetMapping("/getMyLikeBoard")
    public List<Object>  getMyLikeBoard(@RequestHeader("access_token") String token){
        return findMyBoardService.getLikeBoard(token);
    }

    @Operation(summary = "스크랩한 게시물 조회")
    @GetMapping("/myScrapAnimal")
    public ResponseEntity<?> getMyScrapAnimals(@RequestHeader("access_token") String token){
        List<AnimalScrap> getMyScrapAnimals = myPageService.getMyScrapAnimals(token);
        List<MyScrapAnimalResponseDTO> responseList = myPageService.myScrapAnimalResponse(getMyScrapAnimals);
        return ResponseEntity.status(200).body(responseList);
    }
}
