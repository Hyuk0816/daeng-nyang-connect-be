package com.git.backend.daeng_nyang_connect.mypet.comments.controller;

import com.git.backend.daeng_nyang_connect.mypet.comments.dto.MyPetCommentsDTO;
import com.git.backend.daeng_nyang_connect.mypet.comments.service.MyPetCommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Cacheable
@RestController
@Tag(name = "나의 댕냥이 댓글 API")
@RequestMapping("/api/my_pet/comments")
public class MyPetCommentsController {

    private final MyPetCommentsService myPetCommentsService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/post")
    public Map<?, ?> uploadComment(@RequestHeader("access_token") String token,
                                           @RequestParam("id")Long myPet,
                                           @RequestBody MyPetCommentsDTO myPetCommentsDTO){
        return myPetCommentsService.postComment(token, myPet, myPetCommentsDTO);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/modify")
    public Map<?, ?> updateComment(@RequestHeader("access_token") String token,
                                           @RequestParam("id")Long myPetCommentsId,
                                           @RequestBody MyPetCommentsDTO myPetCommentsDTO){
        return myPetCommentsService.modifyComment(token, myPetCommentsId, myPetCommentsDTO);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/delete")
    public Map<?, ?> deleteComment(@RequestHeader("access_token") String token,
                                           @RequestParam("id")Long myPetCommentsId){
        return myPetCommentsService.deleteComment(token, myPetCommentsId);
    }

    @Operation(summary = "댓글 좋아요")
    @PostMapping("/like")
    public Map<?, ?> addLike(@RequestHeader("access_token") String token,
                             @RequestParam("id")Long myPetCommentsId){
        return myPetCommentsService.clickLike(myPetCommentsId, token);
    }

}
