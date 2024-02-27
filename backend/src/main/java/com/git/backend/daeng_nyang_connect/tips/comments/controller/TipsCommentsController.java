package com.git.backend.daeng_nyang_connect.tips.comments.controller;

import com.git.backend.daeng_nyang_connect.tips.comments.dto.TipsCommentsDto;
import com.git.backend.daeng_nyang_connect.tips.comments.service.TipsCommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@Cacheable
@Tag(name = "꿀팁 댓글 API")
@RequestMapping("/api/tips/comments")
public class TipsCommentsController {

    private final TipsCommentsService tipsCommentsService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/post")
    public ResponseEntity<?> postComments(@RequestParam("id")Long tipsId,
                                       @RequestHeader("access_token")String token,
                                       @RequestBody TipsCommentsDto tipsCommentsDto){

        return tipsCommentsService.postComment(token, tipsId, tipsCommentsDto);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/modify")
    public Map<?,?>modifyComments(@RequestHeader("access_token")String token,
                                  @RequestParam("id")Long tipsCommetnsId,
                                  @RequestBody TipsCommentsDto tipsCommentsDto){

        return tipsCommentsService.modifyComment(token, tipsCommetnsId, tipsCommentsDto);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/delete")
    public Map<?,?>deleteComment(@RequestHeader("access_token")String token,
                                  @RequestParam("id")Long tipsCommetnsId){

        return tipsCommentsService.deleteComment(token, tipsCommetnsId);
    }

    @Operation(summary = "댓글 좋아요")
    @PostMapping("/like")
    public Map<?,?>like(@RequestHeader("access_token")String token,
                        @RequestParam("id")Long tipsCommentsId){

        return tipsCommentsService.clickLike(tipsCommentsId, token);
    }
}
