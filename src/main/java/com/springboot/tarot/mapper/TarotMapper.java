package com.springboot.tarot.mapper;

import com.springboot.tarot.dto.TarotDto;
import com.springboot.tarot.entity.Tarot;
import com.springboot.tarot.entity.TarotCategory;
import org.mapstruct.*;


import java.util.*;

@Mapper(componentModel = "spring")
public interface TarotMapper {


    @Mapping(target = "category", source = "gptResult", qualifiedByName = "mapCategory")
    @Mapping(target = "summary", source = "gptResult", qualifiedByName = "mapSummary")
    @Mapping(target = "result", source = "gptResult", qualifiedByName = "mapResult")
    @Mapping(target = "firstCard", source = "cards[0].name")
    @Mapping(target = "firstCardMeaning", source = "cards[0].meaning")
    @Mapping(target = "secondCard", source = "cards[1].name")
    @Mapping(target = "secondCardMeaning", source = "cards[1].meaning")
    @Mapping(target = "thirdCard", source = "cards[2].name")
    @Mapping(target = "thirdCardMeaning", source = "cards[2].meaning")
    TarotDto.Response toResponseDto(TarotCategory category, List<Tarot> cards, Map<String, Object> gptResult);

    @Named("mapCategory")
    static String mapCategory(Map<String, Object> gptResult) {
        return (String) gptResult.getOrDefault("category", "");
    }

    @Named("mapSummary")
    static String mapSummary(Map<String, Object> gptResult) {
        return (String) gptResult.getOrDefault("summary", "");
    }

    @Named("mapResult")
    static String mapResult(Map<String, Object> gptResult) {
        return (String) gptResult.getOrDefault("result", "");
    }

}
