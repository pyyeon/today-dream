package com.springboot.interpretation.dto;



import com.springboot.interpretation.entity.Interpretation;
import com.springboot.interpretation.entity.Interpretation_Mood_Keyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterpretationResponseDto {
    private long interpretationId;
    private String content;
    private String summary;
    private String advice;
    private Interpretation_Mood_Keyword_ResponseDto keyword;
}
