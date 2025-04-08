package org.sopt.domain;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private String title;
    private LocalDateTime createdAt;


    public Post(Long id, String title, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 정적 팩토리 메소드
    public static Post makePost(Long id, String title) {
        Post post = new Post(id, title, LocalDateTime.now());
        return post;
    }

    // 게시글 수정 메소드
    public void changeTitle(String title){
        this.title = title;
    }


}