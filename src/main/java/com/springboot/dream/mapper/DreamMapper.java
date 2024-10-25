package com.springboot.dream.mapper;
import com.springboot.comment.dto.CommentDto;
import com.springboot.comment.entity.Comment;

import com.springboot.dream.dto.DreamDto;
import com.springboot.dream.dto.DreamKeywordResponseDto;
import com.springboot.dream.entity.Dream;
import com.springboot.dream.entity.DreamKeyword;
import com.springboot.interpretation.dto.InterpretationResponseDto;
import com.springboot.interpretation.dto.Interpretation_Mood_Keyword_ResponseDto;
import com.springboot.interpretation.entity.Interpretation;
import com.springboot.interpretation.entity.Interpretation_Mood_Keyword;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DreamMapper {

    Dream dreamPatchDtoToDream(DreamDto.Patch dreamPatchDto);
//    default Dream dreamPatchToDream(DreamDto.Patch requestBody){
//        Dream dream = new Dream();
//
//        dream.setDreamId(requestBody.getDreamId());
//        dream.setDreamSecret(requestBody.getDreamSecret());
//
//        return dream;
//    }

    default Dream dreamPostToDream(DreamDto.Post requestBody){
        Dream dream = new Dream();
        dream.setContent(requestBody.getPrompt());
        return dream;
    }
    default DreamDto.Response dreamToDreamResponseDto(Dream dream){
        DreamDto.Response response = new DreamDto.Response();
        response.setContent(dream.getContent());
        response.setDreamId(dream.getDreamId());
        response.setCreatedAt(dream.getCreatedAt());
        response.setModifiedAt(dream.getModifiedAt());
        response.setDreamStatus(dream.getDreamStatus());
        response.setDreamSecret(dream.getDreamSecret());
        response.setDreamKeywords(dreamKeywordListToResponseDtos(dream.getDreamKeywords()));
        response.setViewCount(dream.getViewCount());
        response.setLikeCount(dream.getLikes().size());
        response.setComments(commentsToCommentResponseDtos(dream.getComments()));
        response.setInterpretationResponse(interpretationToResponseDto(dream.getInterpretation()));
        if(!(dream.getMember() == null)){
            response.setMemberId(dream.getMember().getMemberId());
            response.setNickName(dream.getMember().getNickName());
        }else{
            response.setNickName("익명");
        }

        return response;
    }


    List<DreamDto.Response> dreamsToDreamResponseDtos(List<Dream> dreams);

    default CommentDto.Response commentToCommentResponseDto(Comment comment){
        CommentDto.Response response = new CommentDto.Response();
        response.setCommentId(comment.getCommentId());
        response.setDreamId(comment.getDream().getDreamId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setModifiedAt(comment.getModifiedAt());
        response.setMemberId(comment.getMember().getMemberId());
        response.setNickName(comment.getMember().getNickName());

        return response;
    }

    default List<CommentDto.Response> commentsToCommentResponseDtos(List<Comment> comments){
        return comments.stream()
                .map(comment -> commentToCommentResponseDto(comment)).collect(Collectors.toList());
    }


    default List<DreamKeywordResponseDto> dreamKeywordListToResponseDtos(List<DreamKeyword> dreamKeywords){
        return dreamKeywords.stream()
                .map(dreamKeyword -> dreamKeywordToResponseDto(dreamKeyword)).collect(Collectors.toList());
    }

    default DreamKeywordResponseDto dreamKeywordToResponseDto(DreamKeyword dreamKeyword){
        DreamKeywordResponseDto response = new DreamKeywordResponseDto();
        response.setDreamKeywordId(dreamKeyword.getDreamKeywordId());
        response.setName(dreamKeyword.getName());
        response.setDreamId(dreamKeyword.getDream().getDreamId());

        return response;
    }

    default InterpretationResponseDto interpretationToResponseDto (Interpretation interpretation){
        InterpretationResponseDto interpretationResponseDto = new InterpretationResponseDto();
        Dream dream = interpretation.getDream();
        interpretationResponseDto.setInterpretationId(dream.getInterpretation().getInterpretationId());
        interpretationResponseDto.setContent(dream.getInterpretation().getContent());
        interpretationResponseDto.setSummary(dream.getInterpretation().getSummary());
        interpretationResponseDto.setAdvice(dream.getInterpretation().getAdvice());
        interpretationResponseDto.setKeyword(moodKeywordToResponseDto(dream.getInterpretation().getKeyword()));

        return interpretationResponseDto;
    }

    default Interpretation_Mood_Keyword_ResponseDto moodKeywordToResponseDto(Interpretation_Mood_Keyword moodKeyword){
        Interpretation_Mood_Keyword_ResponseDto responseDto = new Interpretation_Mood_Keyword_ResponseDto();
        Interpretation interpretation = moodKeyword.getInterpretation();
        responseDto.setInterpretationMoodKeywordId(interpretation.getKeyword().getMoodKeywordId());
        responseDto.setName(interpretation.getKeyword().getName());

        return responseDto;
    }


}