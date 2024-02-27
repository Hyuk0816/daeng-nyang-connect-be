package com.git.backend.daeng_nyang_connect.mate.board.controller;

import com.git.backend.daeng_nyang_connect.mate.board.dto.MateDTO;
import com.git.backend.daeng_nyang_connect.mate.board.dto.MateResponseDTO;
import com.git.backend.daeng_nyang_connect.mate.board.service.MateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Cacheable
@RestController
@Tag(name = "댕냥메이트 게시물 API")
@RequestMapping("/api/mate")
public class MateController {

    private final MateService mateService;

    @Operation(summary = "게시물 전체 조회")
    @GetMapping("/getAll")
    public List<MateResponseDTO> findAllMates(Pageable pageable) {
        return mateService.findAllMates(pageable);
    }

    @Operation(summary = "특정 게시물 조회")
    @GetMapping("/getBoard")
    public MateResponseDTO getThisBoard(@RequestParam("id") Long mate) {
        return mateService.getThisBoard(mate);
    }

    @Operation(summary = "게시물 검색")
    @GetMapping("/search")
    public List<MateResponseDTO> searchBoard(@RequestParam String keyword, Pageable pageable) {
        return mateService.searchBoard(keyword, pageable);
    }

    @Operation(summary = "게시물 작성")
    @PostMapping("/post")
    public Map<?,?> postMate(@RequestHeader("access_token") String token,
                             MateDTO mateDTO,
                             List<MultipartFile> files){
        return mateService.postMate(mateDTO, token, files);
    }

    @Operation(summary = "게시물 수정")
    @PutMapping("/modify")
    public Map<?,?> modifyMate(@RequestHeader("access_token") String token,
                               @RequestParam("mateId") Long mateId,
                               MateDTO mateDTO) {
        return mateService.modifyMate(mateId, mateDTO, token);
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/delete")
    public Map<?, ?> deleteMate(@RequestHeader("access_token") String token,
                                @RequestParam("mateId") Long mateId){
        return mateService.deleteMate(mateId, token);
    }

    @Transactional
    @Operation(summary = "게시물 좋아요")
    @PostMapping("/like")
    public Map<String, String> addLike(@RequestHeader("access_token") String token,
                                       @RequestParam("mateId") Long mateId) {
        return mateService.clickLike(mateId, token);
    }

    @Operation(summary = "게시물 사이즈")
    @GetMapping("/getSize")
    public Map<String,Integer>getSize(){
        return mateService.getSize();
    }

}