package com.springboot.dream.entity;


import com.springboot.comment.entity.Comment;

import com.springboot.interpretation.entity.Interpretation;
import com.springboot.like.entity.Like;
import com.springboot.member.entity.Member;
import com.springboot.sharing.entity.Sharing;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity(name = "Dreams")
@NoArgsConstructor
public class Dream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dreamId;

    @Column(length = 500, nullable = false)
    private String content;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DreamStatus dreamStatus = DreamStatus.DREAM_ACTIVE;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DreamSecret dreamSecret = DreamSecret.DREAM_PUBLIC;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "LAST_MODIFIED_AT")
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "dream", cascade = CascadeType.PERSIST)
    private List<Comment> comments = new ArrayList<>();


    @OneToMany(mappedBy = "dream", cascade = CascadeType.PERSIST)
    private List<DreamKeyword> dreamKeywords = new ArrayList<>();

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "interpretation_id")
    private Interpretation interpretation;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "dream", cascade = CascadeType.ALL)
    List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "dream", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<View> views = new ArrayList<>();

    @Column
    private Integer likeCount = 0;

    @Column
    private Integer viewCount = 0;

    public void addDreamKeywords(DreamKeyword dreamKeyword){
        this.dreamKeywords.add(dreamKeyword);
        if(dreamKeyword.getDream() != this){
            dreamKeyword.addDream(this);
        }
    }


    public void addComments(Comment comment){
        this.comments.add(comment);
        if(comment.getDream() != this){
            comment.addDream(this);
        }
    }

    public void setInterpretation(Interpretation interpretation){
        this.interpretation = interpretation;
        if(interpretation.getDream() != this){
            interpretation.setDream(this);
        }
    }

    public void setMember(Member member){
        this.member = member;
        if(member.getDreams().contains(this)){
            member.addDream(this);
        }
    }
    
    @OneToMany(mappedBy = "dream", cascade = CascadeType.PERSIST)
    private List<Sharing> sharingList = new ArrayList<>();

    public void addView(View view) {
        if (!this.views.contains(view)) {
            this.views.add(view);  // 중복 추가 방지
            if (view.getDream() != this) {
                view.setDream(this);  // 역참조 설정
            }
        }
    }


    public void setShareList(Sharing sharing) {
        sharingList.add(sharing);
        if (sharing.getDream() != this) {
            sharing.setDream(this);
        }
    }
//    @OneToMany(mappedBy = "dream")
//    private List<Like> likes = new ArrayList<>();
//
//
//    @OneToMany(mappedBy = "dream")
//    private List<View> views = new ArrayList<>();
//
//    public void setLike(Like like) {
//        likes.add(like);
//        if (like.getDream() != this) {
//            like.setDream(this);
//        }
//    }
//
//
//    public void removeLike(Like like) {
//        this.likes.remove(like);
//        if (like.getDream() == this){
//            like.removeDream(this);
//        }
//    }
//
//    public void setView(View view) {
//        views.add(view);
//        if (view.getDream() != this) {
//            view.setDream(this);
//        }
//    }


    public enum DreamStatus {
        DREAM_ACTIVE("꿈 활성화"),
        DREAM_DEACTIVE("꿈 비활성화");

        @Getter
        private String status;

        DreamStatus(String status) {this.status = status;}
    }

    public enum DreamSecret {
        DREAM_PRIVATE("비밀 꿈"),
        DREAM_PUBLIC("공개 꿈");

        @Getter
        private String status;

        DreamSecret(String status) {this.status = status;}
    }


}
