package com.git.backend.daeng_nyang_connect.mypet.board.controller;

import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetResponseDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.service.MyPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Cacheable
@RestController
@Tag(name = "나의 댕냥이 게시물 API")
@RequestMapping("/api/my_pet")
public class MyPetController {

    private final MyPetService myPetService;

    @Operation(summary = "게시물 전체 조회")
    @GetMapping("/getAll")
    public List<MyPetResponseDTO> findAllMyPet() {
        return myPetService.findAllMyPet();
    }

    @Operation(summary = "특정 게시물 조회")
    @GetMapping("/getBoard")
    public MyPetResponseDTO getThisBoard(@RequestParam("id") Long myPet) {
        return myPetService.getThisBoard(myPet);
    }

    @Operation(summary = "게시물 검색")
    @GetMapping("/search")
    public List<MyPetResponseDTO> searchBoard(@RequestParam String keyword){
        return myPetService.searchBoard(keyword);
    }

    @Operation(summary = "게시물 작성")
    @PostMapping("/post")
    public Map<?,?> postMyPet(@RequestHeader("access_token") String token,
                              MyPetDTO myPetDTO,
                              List<MultipartFile> files){
        return myPetService.postMyPet(myPetDTO, token, files);
    }

    @Operation(summary = "게시물 수정")
    @PutMapping("/modify")
    public Map<?,?> modifyMyPet(@RequestHeader("access_token") String token,
                                @RequestParam("myPetId") Long myPetId,
                                MyPetDTO myPetDTO) {
        return myPetService.modifyMyPet(myPetId, myPetDTO, token);
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/delete")
    public Map<?, ?> deleteMyPet(@RequestHeader("access_token") String token,
                                 @RequestParam("myPetId") Long myPetId){
        return myPetService.deleteMyPet(myPetId, token);
    }

    @Transactional
    @Operation(summary = "게시물 좋아요")
    @PostMapping("/like")
    public Map<String, String> addLike(@RequestHeader("access_token") String token,
                                       @RequestParam("myPetId") Long myPetId) {
        return myPetService.clickLike(myPetId, token);
    }

    @Operation(summary = "게시물 사이즈")
    @GetMapping("/getSize")
    public Map<String,Integer>getSize(){
        return myPetService.getSize();
    }

}
