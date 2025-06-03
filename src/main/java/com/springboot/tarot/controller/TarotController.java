package com.springboot.tarot.controller;
import com.springboot.response.SingleResponseDto;
import com.springboot.tarot.dto.TarotDto;
import com.springboot.tarot.entity.Tarot;
import com.springboot.tarot.entity.TarotCategory;
import com.springboot.tarot.mapper.TarotMapper;
import com.springboot.tarot.service.TarotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;

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
        // PostDto -> TarotCategory 변환
        TarotCategory category = mapper.postDtoToTarotCategory(inputCategory);

        // 타로카드 3장 뽑기
        List<Tarot> cards = tarotService.playTarot(category);

        // GPT 해석 결과 가져오기
        Map<String, Object> gptResult = tarotService.getTarotInterpretation(category, cards);

        // DTO 매핑
        TarotDto.Response responseDto = mapper.toResponseDto(category, cards, gptResult);

        return new ResponseEntity<>(new SingleResponseDto<>(responseDto), HttpStatus.OK);
    }

    // GPT 해석 결과를 가져오는 메서드

}

