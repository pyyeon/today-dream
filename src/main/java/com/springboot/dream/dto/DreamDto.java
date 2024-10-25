package com.springboot.dream.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.comment.dto.CommentDto;
import com.springboot.comment.entity.Comment;
import com.springboot.dream.entity.Dream;
import com.springboot.dream.entity.DreamKeyword;
import com.springboot.interpretation.dto.InterpretationResponseDto;
import com.springboot.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;


public class DreamDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        @NotNull(message = "Prompt must not be null")
        String prompt;
    }

    @Getter
    public static class Patch{

        private long dreamId;

        Dream.DreamSecret dreamSecret;

        public void setDreamId(long dreamId){
            this.dreamId = dreamId;
        }

    }


    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private long dreamId;

        private long memberId;

        private String nickName;

        private String content;
        private Dream.DreamStatus dreamStatus;
        private Dream.DreamSecret dreamSecret;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime modifiedAt;

        private Integer viewCount;
        private Integer likeCount;
        private List<DreamKeywordResponseDto> dreamKeywords;
        private InterpretationResponseDto interpretationResponse;

        private List<CommentDto.Response> comments;


        public String getDreamStatus() {
            return dreamStatus.getStatus();
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseThree {
        private long dreamId;
        private String content;
        private LocalDateTime createdAt;
    }
}
