package org.sopt.domain;

import jakarta.persistence.*;

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

    public Post(){

    }

    public Post(String title) {
        this.title = title;
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


}