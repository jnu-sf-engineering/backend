package com.momentum.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "USER")
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @Column(name = "EMAIL",
            length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD",
            length = 255, nullable = false)
    private String password;

    @Column(name = "NICKNAME",
            length = 255, nullable = false)
    private String nickname;

    @Column(name = "DISCORD",
            length = 255, nullable = false)
    private String discord;

    @Builder
    public User(String email, String password, String nickname, String discord) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.discord = discord;
    }
}
