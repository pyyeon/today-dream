package com.springboot.comment.controller;

import com.springboot.auth.service.AuthService;
import com.springboot.comment.dto.CommentDto;
import com.springboot.comment.entity.Comment;
import com.springboot.comment.service.CommentService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import com.springboot.comment.mapper.CommentMapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@Validated
@Slf4j
@RequestMapping("/")
@RestController
public class CommentController {

    private final CommentMapper mapper;
    private final CommentService commentService;
    private final AuthService authService;

    public CommentController(CommentMapper mapper, CommentService commentService, AuthService authService) {
        this.mapper = mapper;
        this.commentService = commentService;
        this.authService = authService;
    }

    @PostMapping("/dreams/{dream-id}/comments")
    public ResponseEntity postComment(@PathVariable("dream-id") @Positive long dreamId,
                                      @RequestBody CommentDto.Post commentPostDto,
                                      Authentication authentication){
        commentPostDto.setDreamId(dreamId);
        String email = authentication.getName();

        Comment comment = commentService.postComment(mapper.commentPostDtoToComment(commentPostDto), email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/comments/{comment-id}")
    public ResponseEntity patchComment(@PathVariable("comment-id") @Positive long commentId,
                                       @Valid @RequestBody CommentDto.Patch commentPatchDto,
                                       Authentication authentication){
        commentPatchDto.setCommentId(commentId);

        String email = authentication.getName();


        Comment comment = commentService.updateComment(mapper.commentPatchDtoToComment(commentPatchDto),email);

        return new ResponseEntity<>(
                new SingleResponseDto(mapper.commentToCommentResponseDto(comment))
                , HttpStatus.OK);
    }

    @GetMapping("/dreams/{dream-id}/comments")
    public ResponseEntity getComments(@PathVariable("dream-id") @Positive long dreamId,
                                      @Positive @RequestParam int page,
                                      @Positive @RequestParam int size){
        Page<Comment> pageComments = commentService.findComments(dreamId,page-1,size);
        List<Comment> commentList = pageComments.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto(mapper.commentsToCommentResponseDtos(commentList),pageComments),
                HttpStatus.OK);
    }

    @DeleteMapping("/comments/{comment-id}")
    public ResponseEntity deleteComment(@PathVariable("comment-id") @Positive long commentId,
                                        Authentication authentication){

        String email = authentication.getName();
        commentService.deleteComment(commentId, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}