package com.springboot.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberRewardPictureDto {
    @Getter
    @AllArgsConstructor
    public static class Post{
        private long memberId;
        private long rewardPictureId;
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @Setter
    public static class Response{
        private Long memberRewardPictureId;
        private Long rewardPictureId; // 보상 사진의 ID
        private String rewardUrl; // 보상 사진의 URL
    }

}
