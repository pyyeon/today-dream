package com.springboot.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private long dreamId;

        @NotNull(message = "must not be null")
        String content;

    }
    @Getter
    public static class Patch {

        private long commentId;

        @NotNull(message = "must not be null")
        private String content;

        public void setCommentId(long commentId){
            this.commentId = commentId;
        }
    }



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{

        private long commentId;

        private long memberId;

        private String nickName;

        private long dreamId;

        @NotNull(message = "Prompt must not be null")
        String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime modifiedAt;
    }


}