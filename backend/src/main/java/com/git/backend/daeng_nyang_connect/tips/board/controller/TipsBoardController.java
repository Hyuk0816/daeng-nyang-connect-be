package com.git.backend.daeng_nyang_connect.tips.board.controller;



import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDetailDto;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDto;


import com.git.backend.daeng_nyang_connect.tips.board.service.TipsBoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@Cacheable
@Tag(name = "꿀팁 게시물 API")
@RequestMapping("/api/tips")
public class TipsBoardController {

    private final TipsBoardService tipsBoardService;

    @Operation(summary = "게시물 작성")
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<?,?> upload(@RequestHeader("access_token")String token,
                           @ModelAttribute TipsBoardDto tipsBoardDto,
                           List<MultipartFile> files){
       return tipsBoardService.postBoard(tipsBoardDto, token, files);

    }

    @Operation(summary = "게시물 좋아요")
    @PostMapping("/like")
    public ResponseEntity<String > addLike(@RequestHeader("access_token")String token,
                                           @RequestParam("tipsId") Long tipsID){
        return tipsBoardService.clickLike(tipsID, token);
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/delete")
    public Map<String ,String> delete(@RequestHeader("access_token")String token,
                                    @RequestParam("tipsId")Long tipsId){
       return tipsBoardService.delete(token, tipsId);
    }

    @Operation(summary = "게시물 수정")
    @PutMapping("/modify")
    public Map<String ,String> modify(@RequestHeader("access_token")String token,
                                      @RequestParam("tipsId")Long tipsId,
//                                     @RequestParam("tipsImgId")Long tipsImgId,
                                      TipsBoardDto tipsBoardDto)  {
        return tipsBoardService.modifyTipsNoImg(token,tipsId,tipsBoardDto);
    }

    @Operation(summary = "게시물 전체 조회")
    @GetMapping("/getAll")
    public List<TipsBoardDto> getAll(Pageable pageable){
       return tipsBoardService.getAll(pageable);
    }

    @Operation(summary = "특정 게시물 조회")
    @GetMapping("/getBoard")
    public TipsBoardDetailDto getThisBoard(@RequestParam("id")Long tipsId){
        return tipsBoardService.getThisBoard(tipsId);
    }

    @Operation(summary = "게시물 검색")
    @GetMapping("/search")
    public List<TipsBoardDto> searchBoard(@RequestParam String keyword){
        return tipsBoardService.searchBoard(keyword);
    }

    @Operation(summary = "게시물 사이즈")
    @GetMapping("/getSize")
    public Map<String ,Integer> getSize(){
        return tipsBoardService.getSize();
    }

}
