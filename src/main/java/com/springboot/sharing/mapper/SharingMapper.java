package com.springboot.sharing.mapper;

import com.springboot.dream.entity.Dream;
import com.springboot.sharing.dto.SharingDto;
import com.springboot.sharing.entity.Sharing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SharingMapper {
    @Mapping(target = "shareId", ignore = true) // DB에서 자동 생성되므로 무시
    @Mapping(target = "sharingDate", expression = "java(java.time.LocalDateTime.now())") // 현재 시간으로 설정
    @Mapping(source = "requestBody.dreamId", target = "dream.dreamId") // dreamId를 dream의 id 필드로 매핑
    Sharing sharingPostToSharing(SharingDto.Post requestBody);
}
