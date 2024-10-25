package com.springboot.interpretation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Interpretation_Mood_Keyword_ResponseDto {
    private long interpretationMoodKeywordId;
    private String name;
}
