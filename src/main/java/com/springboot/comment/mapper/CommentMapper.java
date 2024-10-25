package com.springboot.comment.mapper;


import com.springboot.comment.dto.CommentDto;
import com.springboot.comment.entity.Comment;
import com.springboot.dream.entity.Dream;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    default Comment commentPostDtoToComment(CommentDto.Post requestBody){
        Dream dream = new Dream();
        dream.setDreamId(requestBody.getDreamId());
        Comment comment = new Comment();
        comment.setContent(requestBody.getContent());
        comment.setDream(dream);

        return comment;
    }
    default Comment commentPatchDtoToComment(CommentDto.Patch requestBody){
        Comment comment = new Comment();
        comment.setContent(requestBody.getContent());
        comment.setCommentId(requestBody.getCommentId());

        return comment;
    }
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
}