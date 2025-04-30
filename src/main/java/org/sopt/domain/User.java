package org.sopt.domain;


import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @OneToMany(mappedBy = "user")
    private ArrayList<Post> posts = new ArrayList<>();


    protected User(){

    }

    public User( String name, String email) {
        this.name = name;
        this.email = email;
    }
}
