package com.springboot.picture.controller;


import com.springboot.member.dto.MemberDto;
import com.springboot.picture.dto.RewardPicturePostDto;
import com.springboot.picture.entity.RewardPicture;
import com.springboot.picture.mapper.RewardPictureMapper;
import com.springboot.picture.service.RewardPictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping("/rewardpictures")
public class RewardPictureController {
    private final RewardPictureMapper mapper;
    private final RewardPictureService rewardPictureService;

    public RewardPictureController(RewardPictureMapper mapper, RewardPictureService rewardPictureService) {
        this.mapper = mapper;
        this.rewardPictureService = rewardPictureService;
    }

    @PostMapping
    public ResponseEntity postRewardPicture(@Valid @RequestBody RewardPicturePostDto requestBody) {
        RewardPicture rewardPicture = rewardPictureService.createRewardPicture(mapper.rewardPicturePostDtoToRewardPicture(requestBody));

        return new ResponseEntity(HttpStatus.OK);
    }

}
