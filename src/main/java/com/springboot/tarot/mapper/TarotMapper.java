package com.springboot.tarot.mapper;

import com.springboot.tarot.dto.TarotDto;
import com.springboot.tarot.entity.Tarot;
import com.springboot.tarot.entity.TarotCategory;
import org.mapstruct.*;


import java.util.*;

@Mapper(componentModel = "spring")
public interface TarotMapper {


    // Post DTO를 TarotCategory로 변환하는 매핑 메서드
    @Mapping(target = "categoryName", source = "inputCategory.category")
    TarotCategory postDtoToTarotCategory(TarotDto.Post inputCategory);

    @Mapping(target = "firstCard", expression = "java(cards.get(0).getName())")
    @Mapping(target = "firstCardMeaning", expression = "java(cards.get(0).getMeaning())")
    @Mapping(target = "secondCard", expression = "java(cards.get(1).getName())")
    @Mapping(target = "secondCardMeaning", expression = "java(cards.get(1).getMeaning())")
    @Mapping(target = "thirdCard", expression = "java(cards.get(2).getName())")
    @Mapping(target = "thirdCardMeaning", expression = "java(cards.get(2).getMeaning())")
    TarotDto.Response toResponseDto(TarotCategory category, List<Tarot> cards, Map<String, Object> gptResult);


}
