package com.agilelens.domain;

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
    private byte sprint_count;

    @Column(name = "MANAGER",
            length = 255, nullable = false)
    private String manager;

    @OneToMany(mappedBy = "project")
    List<Sprint> sprints = new ArrayList<>();

    @Builder
    public Project(User user, String name, String manager) {
        this.user = user;
        this.name = name;
        this.manager = manager;
    }
    public void addSprint(Sprint sprint) {
        this.sprints.add(sprint);
        this.sprint_count++;
    }
}
