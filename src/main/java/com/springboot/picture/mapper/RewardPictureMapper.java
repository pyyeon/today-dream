package com.springboot.picture.mapper;

import com.springboot.picture.dto.RewardPicturePostDto;
import com.springboot.picture.dto.RewardPictureResponseDto;
import com.springboot.picture.entity.RewardPicture;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RewardPictureMapper {
    RewardPicture rewardPicturePostDtoToRewardPicture(RewardPicturePostDto rewardPicturePostDto);
    RewardPictureResponseDto rewardPictureToRewardPictureResponseDto(RewardPicture rewardPicture);

}
