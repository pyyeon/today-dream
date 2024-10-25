package com.springboot.comment.service;

import com.springboot.comment.entity.Comment;
import com.springboot.comment.repository.CommentRepository;
import com.springboot.dream.entity.Dream;
import com.springboot.dream.service.DreamService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final DreamService dreamService;
    private final MemberService memberService;

    public CommentService(CommentRepository commentRepository, DreamService dreamService, MemberService memberService) {
        this.commentRepository = commentRepository;
        this.dreamService = dreamService;
        this.memberService = memberService;
    }

    public Comment postComment(Comment comment, String email){

        Dream findDream = dreamService.findVerifiedDream(comment.getDream().getDreamId());
        Member findMember = memberService.findVerifiedMember(email);
        if(findDream.getDreamStatus() == Dream.DreamStatus.DREAM_DEACTIVE || findDream.getDreamSecret() == Dream.DreamSecret.DREAM_PRIVATE){
            throw new BusinessLogicException(ExceptionCode.CANNOT_REGISTER_COMMENT);
        }
        comment.addMember(findMember);
        comment.addDream(findDream);
        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment, String email){
        Comment findComment = findComment(comment.getCommentId());

        if(!findComment.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_COMMENT);
        }

        Optional.ofNullable(comment.getContent())
                .ifPresent(content -> findComment.setContent(content));
        findComment.setModifiedAt(LocalDateTime.now());

        return commentRepository.save(findComment);
    }

    public Comment findComment(long commentId){
        return findVerifiedComment(commentId);
    }

    public Page<Comment> findComments(long dreamId, int page, int size){
        return commentRepository.findByCommentStatusAndDream_DreamId(Comment.CommentStatus.COMMENT_ACTIVE, dreamId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public void deleteComment(long commentId, String email){
        Comment findComment = findComment(commentId);
        memberService.findVerifiedMember(findComment.getMember().getEmail());
        dreamService.findVerifiedDream(findComment.getDream().getDreamId());

        if(!findComment.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_COMMENT);
        }
        findComment.setCommentStatus(Comment.CommentStatus.COMMENT_DEACTIVE);
        commentRepository.save(findComment);
    }

    public Comment findVerifiedComment(long commentId){
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        Comment findComment = optionalComment.orElseThrow(() ->new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        return findComment;
    }

}