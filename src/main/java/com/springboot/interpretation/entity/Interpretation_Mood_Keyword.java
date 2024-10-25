package com.springboot.interpretation.entity;


import com.springboot.member.entity.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Interpretation_Mood_Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moodKeywordId;

    @Column(nullable = false, updatable = false, unique = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "INTERPRETATIONS_ID")
    private Interpretation interpretation;


    public void setInterpretation(Interpretation interpretation){
        this.interpretation = interpretation;
        if(interpretation.getKeyword() != this){
            interpretation.setKeyword(this);
        }
    }

//    public void setMember(Interpretation interpretation) {
//        this.interpretation = interpretation;
//        if (interpretation.getKeyword() != this) {
//            interpretation.setKeyword(this);
//        }
//    }

}
