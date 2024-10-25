package com.springboot.comment.entity;


import com.springboot.dream.entity.Dream;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "Comments")
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @Column(length = 100, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "DREAM_ID")
    private Dream dream;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Comment.CommentStatus commentStatus = CommentStatus.COMMENT_ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "LAST_MODIFIED_AT")
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void addDream(Dream dream) {
        this.dream = dream;
        if (!this.dream.getComments().contains(this)) {
            this.dream.getComments().add(this);
        }
    }

    public void addMember(Member member){
        this.member = member;
        if(!this.member.getComments().contains(this)){
            this.member.getComments().add(this);
        }
    }

    public enum CommentStatus {
        COMMENT_ACTIVE("댓글 활성화"),
        COMMENT_DEACTIVE("댓글 비활성화");

        @Getter
        private String status;

        CommentStatus(String status) {this.status = status;}
    }


}
