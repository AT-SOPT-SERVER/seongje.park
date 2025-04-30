package org.sopt.domain;

import jakarta.persistence.*;
import org.sopt.domain.enums.Tag;

import java.time.LocalDateTime;

@Entity
public class Post {

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


    protected Post(){

    }

    public Post(User user, String title , String content, Tag tag) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.tag = tag;
        assignUser(user); // 연관관계 편의 메소드 설정
    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }
}