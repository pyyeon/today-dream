package com.springboot.tarot.controller;

import com.springboot.response.SingleResponseDto;
import com.springboot.tarot.dto.TarotDto;
import com.springboot.tarot.entity.Tarot;
import com.springboot.tarot.entity.TarotCategory;
import com.springboot.tarot.mapper.TarotMapper;
import com.springboot.tarot.service.TarotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/tarots")
@Validated
@Slf4j
public class TarotController {

    private final TarotService tarotService;
    private final TarotMapper mapper;


    public TarotController(TarotService tarotService, TarotMapper mapper) {
        this.tarotService = tarotService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postTarot(@Valid @RequestBody TarotDto.Post inputCategory) {

        //어떤매퍼가 필요하니
        //제이슨을 받으면 카테고리로 바꿔주는 매퍼 필요해
        TarotCategory category = mapper.postDtoToTarotCategory(inputCategory);

        TarotDto.Response response = tarotService.playTarot(category);


        //mapping으로 카테고리 받으면 포스트디티오 -> 카테고리 로 변환
        return new ResponseEntity(new SingleResponseDto<>(response), HttpStatus.OK);

}
}
