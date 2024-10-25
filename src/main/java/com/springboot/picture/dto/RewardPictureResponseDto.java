package com.springboot.picture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardPictureResponseDto {
    private Long rewardPictureId;
    private String rewardUrl;
}
