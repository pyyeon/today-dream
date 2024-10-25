package com.springboot.like.service;

import com.springboot.dream.entity.Dream;
import com.springboot.dream.service.DreamService;
import com.springboot.like.entity.Like;
import com.springboot.like.repository.LikeRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberService memberService;
    private final DreamService dreamService;

    public LikeService(LikeRepository likeRepository, MemberService memberService, DreamService dreamService) {
        this.likeRepository = likeRepository;
        this.memberService = memberService;
        this.dreamService = dreamService;
    }

    // public Like createLike(Like like, String email){
    // Member findMember = memberService.findVerifiedMember(email);
    // Dream dream = dreamService.findVerifiedDream(like.getDream().getDreamId());
    // like.setMember(findMember);
    // like.setDream(dream);
    // Optional<Like> optionalLike = likeRepository.findByDreamAndMember(dream,
    // findMember);

    // Integer count = like.getDream().getLikeCount();
    // if(optionalLike.isPresent()){
    // findMember.removeLike(optionalLike.get());
    // likeRepository.delete(optionalLike.get());
    // like.getDream().setLikeCount(count-1);
    // return null;
    // }
    // like.getDream().setLikeCount(count + 1);

    // return likeRepository.save(like);
    // }
    public Like createLike(Like like, String email) {
        Member findMember = memberService.findVerifiedMember(email);
        Dream dream = dreamService.findVerifiedDream(like.getDream().getDreamId());
        like.setMember(findMember);
        like.setDream(dream);
        Optional<Like> optionalLike = likeRepository.findByDreamAndMember(dream, findMember);

        Integer count = like.getDream().getLikeCount();
        if (optionalLike.isPresent()) {
            findMember.removeLike(optionalLike.get());
            dream.getLikes().remove(optionalLike.get());
            likeRepository.delete(optionalLike.get());
            return null;
        }
        like.getDream().setLikeCount(count + 1);

        return likeRepository.save(like);
    }

}
