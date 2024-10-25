package com.springboot.member.mapper;

import com.springboot.dream.dto.DreamDto;
import com.springboot.dream.entity.Dream;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberRewardPictureDto;
import com.springboot.member.entity.Member;
import com.springboot.member.entity.MemberRewardPicture;
import com.springboot.picture.entity.RewardPicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    default Member memberPostToMember(MemberDto.Post requestBody){
        Member member = new Member();
        member.setEmail(requestBody.getEmail());
        member.setPassword(requestBody.getPassword());
        member.setNickName(requestBody.getNickName());

        return member;
    }

    Member memberPatchToMember(MemberDto.Patch requestBody);
    default Member memberPatchPasswordToMember(MemberDto.PatchPassword requestBody){
        Member member = new Member();
        member.setMemberId(requestBody.getMemberId());
        member.setPassword(requestBody.getNewPassword());
        return member;
    }
    Member memberPatchProfileToMember(MemberDto.PatchProfile requestBody);
    MemberDto.Response memberToMemberResponse(Member member);
    default MemberDto.Response memberToMemberResponseMyPage(Member member){
        MemberDto.Response response = new MemberDto.Response();

        List<DreamDto.ResponseThree> dreams = member.getDreams().stream()
                .filter(dream -> !dream.getDreamStatus().equals(Dream.DreamStatus.DREAM_DEACTIVE))
                .limit(3)
                .map(dream -> dreamToDreamResponseThree(dream))
                .collect(Collectors.toList());
        List<MemberRewardPictureDto.Response> pictures = member.getMemberRewardPictures().stream()
                        .map(memberRewardPicture -> memberRewardPictureToMemberRewardPictureDto(memberRewardPicture))
                        .collect(Collectors.toList());
        response.setMemberId(member.getMemberId());
        response.setNickName(member.getNickName());
        response.setDreams(dreams);
        response.setEmail(member.getEmail());
        response.setProfileUrl(member.getProfileUrl());
        response.setPictures(pictures);
        response.setMemberStatus(member.getMemberStatus());
        response.setStampCount(member.getStamp().getCount());

        return response;
    }
    default MemberRewardPictureDto.Response memberRewardPictureToMemberRewardPictureResponseDto(MemberRewardPicture memberRewardPicture){
        MemberRewardPictureDto.Response response = new MemberRewardPictureDto.Response();
        response.setRewardPictureId(memberRewardPicture.getRewardPicture().getRewardPictureId());
        response.setRewardUrl(memberRewardPicture.getRewardPicture().getRewardUrl());

        return response;
    }

    default DreamDto.ResponseThree dreamToDreamResponseThree(Dream dream){
        DreamDto.ResponseThree response = new DreamDto.ResponseThree();
        response.setDreamId(dream.getDreamId());
        response.setContent(dream.getContent());
        response.setCreatedAt(dream.getCreatedAt());

        return response;
    }

//    @Mapping(source = "rewardPicture.rewardPictureId", target = "rewardPictureId")
//    @Mapping(source = "rewardPicture.rewardUrl", target = "rewardUrl")
//    MemberRewardPictureDto.Response memberRewardPictureToMemberRewardPictureDto(MemberRewardPicture memberRewardPicture);
    default MemberRewardPictureDto.Response memberRewardPictureToMemberRewardPictureDto(MemberRewardPicture memberRewardPicture){
        MemberRewardPictureDto.Response response = new MemberRewardPictureDto.Response();
        response.setMemberRewardPictureId(memberRewardPicture.getMemberRewardPictureId());
        response.setRewardUrl(memberRewardPicture.getRewardPicture().getRewardUrl());
        response.setRewardPictureId(memberRewardPicture.getRewardPicture().getRewardPictureId());

        return response;
    }

//    List<MemberDto.Response> membersToMemberResponses(List<Member> members);
}