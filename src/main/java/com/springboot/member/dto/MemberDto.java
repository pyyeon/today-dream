package com.springboot.member.dto;


import com.springboot.dream.dto.DreamDto;
import com.springboot.dream.entity.Dream;
import com.springboot.member.entity.Member;
import com.springboot.validator.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

public class MemberDto {
    @Getter
    @AllArgsConstructor
    public static class Post{
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String nickName;

        @NotBlank
        private String authCode;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PatchProfile{
        private long memberId;
        @NotBlank
        private String profileUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class PostNickName{
        private long memberId;

        @NotSpace(message = "회원 이름은 공백이 아니어야 합니다")
        private String nickName;

    }

    @Getter
    @AllArgsConstructor
    public static class Patch{
        private long memberId;

        @NotSpace(message = "회원 이름은 공백이 아니어야 합니다")
        private String nickName;

        private Member.MemberStatus memberStatus;

        public void setMemberId(long memberId) {
            this.memberId = memberId;
        }

    }

    @Getter
    @AllArgsConstructor
    public static class PatchPassword{
        private long memberId;

        private String password;

        private String newPassword;

        public void setMemberId(long memberId) {
            this.memberId = memberId;
        }

    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @Setter
    public static class Response{
        private long memberId;
        private String email;
        private String nickName;
        private List<DreamDto.ResponseThree> dreams;
        private int stampCount;
        private List<MemberRewardPictureDto.Response> pictures;
        private String profileUrl;
        private Member.MemberStatus memberStatus;

        public String getMemberStatus() {
            return memberStatus.getStatus();
        }

    }

    @Getter
    @AllArgsConstructor
    public static class Check{ private boolean isAvailable;}

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NickName{ private String nickName;}

    @Getter
    @Setter
    @AllArgsConstructor
    public static class EmailCheckDto {
        @Email
        private String email;
    }
}