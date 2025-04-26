package org.sopt.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Post {

    @Id @GeneratedValue
    private Long id;
    private String title;
    private LocalDateTime createdAt;

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