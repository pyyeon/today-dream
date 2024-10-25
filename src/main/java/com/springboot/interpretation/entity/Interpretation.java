package com.springboot.interpretation.entity;

import com.springboot.audit.Auditable;
import com.springboot.dream.entity.Dream;

import com.springboot.member.entity.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "INTERPRETATIONS")
public class Interpretation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interpretationId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private String advice;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "LAST_MODIFIED_AT")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "interpretation", cascade = CascadeType.PERSIST)
    private Dream dream;

    @OneToOne(mappedBy = "interpretation", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Interpretation_Mood_Keyword keyword;

    public void setDream(Dream dream) {
        this.dream = dream;
        if (dream.getInterpretation() != this) {
            dream.setInterpretation(this);
        }
    }
    public void setKeyword(Interpretation_Mood_Keyword keyword){
        this.keyword = keyword;
        if(keyword.getInterpretation() != this){
            keyword.setInterpretation(this);
        }
    }

}


