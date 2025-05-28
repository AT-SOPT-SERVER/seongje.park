package org.sopt.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.BatchSize;
import org.sopt.global.common.entity.BaseEntity;
import org.sopt.domain.enums.Tag;
import org.sopt.domain.like.PostLike;
import org.sopt.dto.post.PostRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * title index
 */
// 인덱스를 어떤 칼럼에 둘 것인가에 대한 고민
// 게시글 제목에 대한 중복 체크를 할때 existsByTitle 로 검사한다.
// 이때, table full scan 이 이루어지므로, title 에 대해 인덱스 설정

/**
 * createdAt index
 */
// 게시글을 최신순으로 정렬할때 , order by 를 통해 외부정렬이 이루어지는데,
// createdAt 으로 인덱스를 만들어놓으면, 정렬을 하지 않아도 됨.
// 인덱스 타고 내려가면, 리프노드쪽에 레코드와, 레코드 포인터가 저장되어 있음
// 얘네들 쭉 타고 포인터 읽기만 하면됨.

/**
 * 작성자별 최신 순 조회 (구현은 안되어 있으나, 추가될 가능성이 있으므로)
 * user_id, createdAt 에 대해 복합 index
 */

/** 작성자별 게시글조회
 * user_id 에 index
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
    @Index(name = "idx_post_title", columnList = "title"),
    @Index(name = "idx_post_user_id", columnList = "USER_ID"),
    @Index(name = "idx_post_created_at", columnList = "createdAt DESC"),
    @Index(name = "idx_post_user_created", columnList = "USER_ID, createdAt DESC")

})
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;


    // 고민해볼점
    // tags 를 값 타입 컬렉션으로 설정할까? 엔티티로 관리할까
    // 게시글은 tag 를 최대 2개까지 가질 수 있음.
    // tag 에 대한 crud 가 빈번하거나, tag 에 추가정보가 엔티티로 승격하는게 맞지만
    // 지금의 요구사항에서는 그렇지 않으므로 값타입 컬렉션으로 간단화하는게 맞다고 판단함
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private List<Tag> tags;

    // 요구사항에 댓글 추가. 게시글에는 댓글이 여러개 있을 수 있다.
    // 게시글이 삭제되면, 댓글도 삭제되어야함

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private Integer likeCount = 0;
    // 게시글에 대한 좋아요의 수
    // 고민해볼점
    // 한 게시글이 받은 좋아요를 리스트로 관리할 필요가 있을까?
    // 즉, 좋아요 수를 출력하는 것 이외에 기능이 필요할까?
    // 좋아요를 누른 유저 목록도 같이 출력하고 싶으면.. 리스트로 관리하는게 맞다.
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();


    @Builder
    public Post(User user, String title , String content, List<Tag> tags) {
        this.title = title;
        this.content = content;
        this.tags = tags;
        assignUser(user); // 연관관계 편의 메소드 설정
    }

    public static Post makePost(User user, PostRequest request) {
        return Post.builder()
            .user(user)
            .title(request.title())
            .content(request.content())
            .tags(request.tags())
            .build();
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