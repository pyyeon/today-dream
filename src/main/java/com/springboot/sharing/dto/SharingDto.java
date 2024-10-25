package com.springboot.sharing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class SharingDto {
    @Getter
    @AllArgsConstructor
    public static class Post {
        @Setter
        private Long dreamId;

    }
}
