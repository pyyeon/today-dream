package com.springboot.tarot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class TarotDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        @NotNull
        String category;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @NotNull
        String category;

        // 🎴 개별 카드 정보 추가
        String firstCard;          // 첫 번째 카드 이름
        String firstCardMeaning;   // 첫 번째 카드 의미
        String secondCard;         // 두 번째 카드 이름
        String secondCardMeaning;  // 두 번째 카드 의미
        String thirdCard;          // 세 번째 카드 이름
        String thirdCardMeaning;   // 세 번째 카드 의미

        // 🔮 운세 요약 및 상세 해석
        String summary;  // 운세 요약 (한 줄)
        String result;   // 상세 해석
    }
}
