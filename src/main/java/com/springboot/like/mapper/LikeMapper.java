package com.springboot.like.mapper;

import com.springboot.dream.entity.Dream;
import com.springboot.like.dto.LikePostDto;
import com.springboot.like.entity.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    default Like likePostDtoToLike(LikePostDto likePostDto){
        Like like = new Like();
        Dream dream = new Dream();
        dream.setDreamId(likePostDto.getDreamId());
        like.setDream(dream);
        return like;
    }
}
