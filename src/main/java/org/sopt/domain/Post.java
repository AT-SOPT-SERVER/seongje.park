package org.sopt.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.sopt.common.entity.BaseEntity;
import org.sopt.domain.enums.Tag;
import org.sopt.domain.like.CommentLike;
import org.sopt.domain.like.PostLike;
import org.sopt.dto.CommentCreateRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Enumerated(EnumType.STRING)
    private Tag tag;

    // 요구사항에 댓글 추가. 게시글에는 댓글이 여러개 있을 수 있다.
    // 게시글이 삭제되면, 댓글도 삭제되어야함
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private Integer likeCount = 0;
    // 게시글에 대한 좋아요의 수
    // 고민해볼점
    // 한 게시글이 받은 좋아요를 리스트로 관리할 필요가 있을까?
    // 즉, 좋아요 수를 출력하는 것 이외에 기능이 필요할까?
    // 좋아요를 누른 유저 목록도 같이 출력하고 싶으면.. 리스트로 관리하는게 맞다.
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();




    public Post(User user, String title , String content, Tag tag) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.tag = tag;
        assignUser(user); // 연관관계 편의 메소드 설정
    }

    public void changeTitle(String title){
        this.title = title;
    }

    // 연관관계 편의 메소드
    public void assignUser(User user){
        this.user = user;
        if (!user.getPosts().contains(this)){
            user.getPosts().add(this);
        }

    }

    // 좋아요 수 증가
    public void addLike() {
        this.likeCount += 1;
    }

    // 좋아요 수 감소
    public void minusLike() {
        this.likeCount -= 1;
    }




}