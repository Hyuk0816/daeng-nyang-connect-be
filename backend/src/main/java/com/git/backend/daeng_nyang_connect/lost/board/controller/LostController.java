package com.git.backend.daeng_nyang_connect.lost.board.controller;

import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.lost.board.dto.LostBoardDTO;
import com.git.backend.daeng_nyang_connect.lost.board.dto.LostBoardDetailDTO;
import com.git.backend.daeng_nyang_connect.lost.board.service.LostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "미아센터 게시물 API")
@RequestMapping("/api/lost")
@Slf4j
@Cacheable
public class LostController {
    private final LostService lostService;

    //lost 등록
    @Operation(summary = "게시물 작성")
    @PostMapping("/post")
    public Map<?,?> addLost(@RequestHeader("access_token") String token,
                             LostBoardDetailDTO lostBoardDetailDTO,
                             List<MultipartFile> files){
        return lostService.addLost(lostBoardDetailDTO, token, files);
    }

    //lost 삭제
    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/delete")
    public Map<String,String> deleteLost(@RequestHeader("access_token")String token,
                                         @RequestParam("id")Long lostBoardId){
        return lostService.deleteLost(token,lostBoardId);
    }

    //lost 수정
    @Operation(summary = "게시물 수정")
    @PutMapping("/modify")
    public Map<String, String> modify(@RequestHeader("access_token") String token,
                                      @RequestParam("id")Long lostBoardId,
                                      LostBoardDetailDTO lostBoardDetailDTO)
        throws FileUploadFailedException {
        return lostService.modifyLost(token, lostBoardId, lostBoardDetailDTO);
    }

    //모든 lost
    @Operation(summary = "게시물 전체 조회")
    @GetMapping("/getAll")
    public List<LostBoardDTO> getAll(){
        return lostService.getAll();
    }

    //하나의 lost
    @Operation(summary = "특정 게시물 조회")
    @GetMapping("/getBoard")
    public LostBoardDetailDTO getOne(@RequestParam("id")Long lostBoardId){
        return lostService.getThis(lostBoardId);
    }

    //검색한 lost
    @Operation(summary = "게시물 검색")
    @GetMapping("/search")
    public List<LostBoardDTO> getSearch(@RequestParam String keyword){
        return lostService.searchLost(keyword);
    }

    @Operation(summary = "게시물 사이즈")
    @GetMapping("/getSize")
    public Map<String,Integer>getSize(){
        return lostService.getSize();
    }
}
