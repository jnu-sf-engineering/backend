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
@Table(name = "PROJECT")
public class Project {
    @Id
    @Column(name = "PROJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="USER_ID",
            nullable = false)
    private User user;

    @Column(name = "PROJECT_NAME",
            length = 255, nullable = false)
    private String name;

    @Column(name = "SPRINT_COUNT",
            nullable = true, columnDefinition = "TINYINT")
    private Byte sprint_count;

    @Column(name = "MANAGER",
            length = 255, nullable = false)
    private String manager;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Sprint> sprints = new ArrayList<>();

    @Builder
    public Project(User user, String name, String manager) {
        this.user = user;
        this.name = name;
        this.manager = manager;
        this.sprint_count = 0;
    }
    public void addSprint(Sprint sprint) {
        if (this.sprint_count == null) {
            this.sprint_count = 0;
        }
        this.sprints.add(sprint);
        this.sprint_count++;
    }
}
