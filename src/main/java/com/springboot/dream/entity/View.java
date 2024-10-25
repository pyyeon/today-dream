package com.springboot.dream.entity;

import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity(name ="VIEWS")
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId;

    @ManyToOne
    @JoinColumn(name = "DREAM_ID")
    private Dream dream;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void setDream(Dream dream){
        this.dream = dream;
        if(!dream.getViews().contains(this)){
            dream.addView(this);
        }
    }

}
