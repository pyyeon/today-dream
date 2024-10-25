package com.springboot.stamp.service;

import com.springboot.member.entity.Member;
import com.springboot.member.entity.MemberRewardPicture;
import com.springboot.member.service.MemberService;
import com.springboot.picture.entity.RewardPicture;
import com.springboot.picture.repository.RewardPictureRepository;
import com.springboot.stamp.entity.Stamp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class StampService {

    private final MemberService memberService;

    public StampService(MemberService memberService) {
        this.memberService = memberService;

    }

    public void incrementStampCount(Member member) {
        // 멤버의 스탬프 가져오기
        Stamp stamp = member.getStamp();

        // 스탬프 카운트 증가
        if (stamp != null) {
            stamp.setCount(stamp.getCount() + 1);
            memberService.updateMember(member); // 업데이트된 멤버 저장
        } else {
            // 스탬프가 없다면 새로 생성
            Stamp newStamp = new Stamp();
            newStamp.setCount(1);
            member.setStamp(newStamp);
            memberService.updateMember(member); // 새 스탬프와 함께 멤버 저장
        }
    }





}
