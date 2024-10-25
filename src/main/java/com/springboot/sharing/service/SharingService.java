package com.springboot.sharing.service;

import com.springboot.dream.entity.Dream;
import com.springboot.dream.service.DreamService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.entity.MemberRewardPicture;
import com.springboot.member.service.MemberService;
import com.springboot.picture.entity.RewardPicture;
import com.springboot.picture.repository.RewardPictureRepository;
import com.springboot.picture.service.RewardPictureService;
import com.springboot.sharing.entity.Sharing;
import com.springboot.sharing.repository.SharingRepository;
import com.springboot.stamp.entity.Stamp;
import com.springboot.stamp.service.StampService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SharingService {


    private final SharingRepository sharingRepository;

    private final MemberService memberService;
    private final DreamService dreamService;
    private final StampService stampService;
    private final RewardPictureRepository rewardPictureRepository;
    public SharingService(SharingRepository sharingRepository, MemberService memberService, DreamService dreamService, StampService stampService, RewardPictureRepository rewardPictureRepository) {

        this.sharingRepository = sharingRepository;
        this.memberService = memberService;
        this.dreamService = dreamService;
        this.stampService = stampService;
        this.rewardPictureRepository = rewardPictureRepository;
    }

    //레포에 저장하기
    //
    public Sharing logSharing(Sharing sharing, String email) {

        Dream dream = dreamService.findVerifiedDream(sharing.getDream().getDreamId());
        sharing.setDream(dream);

        // 유효한 멤버인지 확인 > 드림의 멤버가 유효한 멤버인지
        Member member = memberService.findVerifiedMember(email);
        sharing.setMember(member);

        Sharing saveSharing = sharingRepository.save(sharing);

        stampService.incrementStampCount(dream.getMember());

        Stamp stamp = member.getStamp();
        if (stamp.getCount() % 5 == 0 && stamp.getCount() != 0){
            RewardPicture rewardPicture = getNextRewardPictureForMember(member);
            MemberRewardPicture memberRewardPicture = new MemberRewardPicture();
            memberRewardPicture.setMember(member);
            memberRewardPicture.setRewardPicture(rewardPicture);

            member.addMemberRewardPicture(memberRewardPicture);
            rewardPicture.addMemberRewardPicture(memberRewardPicture);
        }
        return saveSharing;
    }


    private RewardPicture getNextRewardPictureForMember(Member member) {
        // RewardPicture에서 아직 해당 멤버가 받지 않은 보상을 가져옵니다.
        List<RewardPicture> rewardPictures = rewardPictureRepository.findAll();
        List<RewardPicture> receivedPictures = member.getMemberRewardPictures().stream()
                .map(MemberRewardPicture::getRewardPicture)
                .collect(Collectors.toList());

        for (RewardPicture rewardPicture : rewardPictures) {
            if (!receivedPictures.contains(rewardPicture)) {
                return rewardPicture;
            }
        }

        // 만약 받을 수 있는 RewardPicture가 더 이상 없다면, 에러를 발생시키거나 다른 로직을 처리할 수 있습니다.
        throw new RuntimeException("No more rewards available for the member.");
    }


}
