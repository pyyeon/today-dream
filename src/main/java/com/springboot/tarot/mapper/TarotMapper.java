package com.springboot.tarot.mapper;

import com.springboot.tarot.dto.TarotDto;
import com.springboot.tarot.entity.Tarot;
import com.springboot.tarot.entity.TarotCategory;
import org.mapstruct.*;


import java.util.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TarotMapper {

    TarotCategory postDtoToTarotCategory(TarotDto.Post inputCategory);

    // DTO 생성은 수동으로 처리 (MapStruct 기본 매핑은 Map<Object> → DTO 불가)
    default TarotDto.Response toResponseDto(TarotCategory category, List<Tarot> cards, Map<String, Object> gptResult) {
        TarotDto.Response response = new TarotDto.Response();

        response.setCategory(category.getCategoryName());

        if (cards.size() >= 3) {
            response.setFirstCard(cards.get(0).getName());
            response.setFirstCardMeaning(cards.get(0).getMeaning());
            response.setSecondCard(cards.get(1).getName());
            response.setSecondCardMeaning(cards.get(1).getMeaning());
            response.setThirdCard(cards.get(2).getName());
            response.setThirdCardMeaning(cards.get(2).getMeaning());
        }

        response.setSummary((String) gptResult.getOrDefault("summary", ""));
        response.setResult((String) gptResult.getOrDefault("result", ""));

        return response;
    }
}
