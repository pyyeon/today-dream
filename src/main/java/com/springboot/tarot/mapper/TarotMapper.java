package com.springboot.tarot.mapper;

import com.springboot.tarot.dto.TarotDto;
import com.springboot.tarot.entity.TarotCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TarotMapper {


    // Post DTO를 TarotCategory로 변환하는 매핑 메서드
    @Mapping(target = "categoryName", source = "inputCategory.category")
    TarotCategory postDtoToTarotCategory(TarotDto.Post inputCategory);

}
