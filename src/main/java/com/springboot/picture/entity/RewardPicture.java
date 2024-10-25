package com.springboot.picture.entity;

import com.springboot.member.entity.MemberRewardPicture;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class RewardPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardPictureId;

    @Column(nullable = false)
    private String rewardUrl;

    @OneToMany(mappedBy = "rewardPicture")
    private List<MemberRewardPicture> memberRewardPictures= new ArrayList<>();

    public void addMemberRewardPicture(MemberRewardPicture memberRewardPicture) {
        this.memberRewardPictures.add(memberRewardPicture);
        if (memberRewardPicture.getRewardPicture() != this) {
            memberRewardPicture.addRewardPicture(this);
        }
    }

}
