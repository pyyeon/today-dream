package com.springboot.dream.entity;


import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name ="DreamKeywords")
@NoArgsConstructor
public class DreamKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dreamKeywordId;

    @Column(length = 20, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "DREAM_ID")
    private Dream dream;

    public void addDream(Dream dream) {
        this.dream = dream;
        if (!this.dream.getDreamKeywords().contains(this)) {
            this.dream.getDreamKeywords().add(this);
        }
    }


}
