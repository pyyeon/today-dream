package com.springboot.like.controller;

import com.springboot.like.dto.LikePostDto;
import com.springboot.like.entity.Like;
import com.springboot.like.mapper.LikeMapper;
import com.springboot.like.service.LikeService;
import com.springboot.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/")
@Validated
public class LikeController {
    private final static String LIKES_DEFAULT_URL = "/like";
    private final LikeService likeService;
    private final LikeMapper mapper;

    public LikeController(LikeService likeService, LikeMapper mapper) {
        this.likeService = likeService;
        this.mapper = mapper;
    }

    // @PostMapping("/dreams/{dream-id}/likes")
    // public ResponseEntity postLike(@Valid @PathVariable("dream-id") long dreamId,
    // Authentication authentication){
    // LikePostDto likePostDto = new LikePostDto();
    // likePostDto.setDreamId(dreamId);
    // if (authentication == null) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }
    // String email = authentication.getName();
    // Like like = likeService.createLike(mapper.likePostDtoToLike(likePostDto),
    // email);

    // URI location = UriCreator.createUri(LIKES_DEFAULT_URL, like.getLikeId());
    // return ResponseEntity.created(location).build();
    // }
    @PostMapping("/dreams/{dream-id}/likes")
    public ResponseEntity postLike(@Valid @PathVariable("dream-id") long dreamId,
            Authentication authentication) {
        LikePostDto likePostDto = new LikePostDto();
        likePostDto.setDreamId(dreamId);

        if (authentication == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName();
        Like like = likeService.createLike(mapper.likePostDtoToLike(likePostDto), email);

        if (like == null) {
            // Like가 제거된 경우, No Content 상태를 반환
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        URI location = UriCreator.createUri(LIKES_DEFAULT_URL, like.getLikeId());
        return ResponseEntity.created(location).build();
    }

}
