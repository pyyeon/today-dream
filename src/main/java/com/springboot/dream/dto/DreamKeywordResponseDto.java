package com.springboot.dream.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DreamKeywordResponseDto {

    private long dreamKeywordId;
    private String name;
    private long dreamId;

}
