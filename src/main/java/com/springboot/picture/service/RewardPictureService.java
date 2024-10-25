package com.springboot.picture.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.picture.entity.RewardPicture;
import com.springboot.picture.repository.RewardPictureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@Transactional
public class RewardPictureService {
    private final RewardPictureRepository rewardPictureRepository;

    public RewardPictureService(RewardPictureRepository rewardPictureRepository) {
        this.rewardPictureRepository = rewardPictureRepository;
    }

    public RewardPicture createRewardPicture(RewardPicture rewardPicture){

        return rewardPictureRepository.save(rewardPicture);
    }

    public RewardPicture findRewardPicture(long rewardPictureId){
        Optional<RewardPicture> rewardPictureOptional = rewardPictureRepository.findById(rewardPictureId);
        return rewardPictureOptional.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.PICTURE_NOT_FOUND));
    }

}
