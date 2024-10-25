package com.springboot.member.entity;


import com.springboot.picture.entity.RewardPicture;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name="MEMBER_REWARDPICTURE")
@NoArgsConstructor
public class MemberRewardPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberRewardPictureId;

    @ManyToOne
    @JoinColumn(name = "REWARDPICTURE_ID")
    private RewardPicture rewardPicture;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void addMember(Member member){
        this.member = member;
        if(!member.getMemberRewardPictures().contains(this)){
            this.member.getMemberRewardPictures().add(this);
        }
    }

    public void addRewardPicture(RewardPicture rewardPicture){
        this.rewardPicture = rewardPicture;
        if(!this.rewardPicture.getMemberRewardPictures().contains(this)){
            this.rewardPicture.addMemberRewardPicture(this);
        }
    }

}
