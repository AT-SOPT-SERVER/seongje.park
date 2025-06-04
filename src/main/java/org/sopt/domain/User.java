package org.sopt.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.sopt.global.common.entity.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 패스워드 추가
    private String password;

    private String name;

    private String email;

    // User 가 작성한 게시글
    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    // User 가 작성한 댓글
    @OneToMany(mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }
}
