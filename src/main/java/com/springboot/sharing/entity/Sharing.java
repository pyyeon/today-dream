package com.springboot.sharing.entity;

import com.springboot.dream.entity.Dream;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sharing")
@Getter
@Setter
@NoArgsConstructor
public class Sharing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shareId;




    @Column(nullable = false)
    LocalDateTime sharingDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = true)  // nullable을 true로 설정
    Member member;

    @ManyToOne
    @JoinColumn(name = "DREAM_ID", nullable = false)
    Dream dream;


    public void setDream(Dream dream) {
        this.dream = dream;
        if (!dream.getSharingList().contains(this)) {
            dream.setShareList(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
        if (!this.member.getSharings().contains(this)) {
            this.member.getSharings().add(this);
        }
    }

}
