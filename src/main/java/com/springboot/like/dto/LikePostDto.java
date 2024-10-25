package com.springboot.like.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class LikePostDto {
    @Positive
    private Long dreamId;
}
