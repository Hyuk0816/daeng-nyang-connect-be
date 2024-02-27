package com.git.backend.daeng_nyang_connect.mate.comments.controller;

import com.git.backend.daeng_nyang_connect.mate.comments.dto.MateCommentsDTO;
import com.git.backend.daeng_nyang_connect.mate.comments.service.MateCommentsService;
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
@Tag(name = "댕냥메이트 댓글 API")
@RequestMapping("/api/mate/comments")
public class MateCommentsController {

    private final MateCommentsService mateCommentsService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/post")
    public Map<?, ?> postComment(@RequestHeader("access_token") String token,
                                 @RequestParam("id")Long mate,
                                 @RequestBody MateCommentsDTO mateCommentsDTO){
        return mateCommentsService.postComment(token, mate, mateCommentsDTO);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/modify")
    public Map<?, ?> modifyComment(@RequestHeader("access_token") String token,
                                   @RequestParam("id")Long mateCommentsId,
                                   @RequestBody MateCommentsDTO mateCommentsDTO){
        return mateCommentsService.modifyComment(token, mateCommentsId, mateCommentsDTO);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/delete")
    public Map<?, ?> deleteComment(@RequestHeader("access_token") String token,
                                   @RequestParam("id")Long mateCommentsId){
        return mateCommentsService.deleteComment(token, mateCommentsId);
    }

    @Operation(summary = "댓글 좋아요")
    @PostMapping("/like")
    public Map<?, ?> addLike(@RequestHeader("access_token") String token,
                             @RequestParam("id")Long mateCommentsId){
        return mateCommentsService.clickLike(mateCommentsId, token);
    }
}