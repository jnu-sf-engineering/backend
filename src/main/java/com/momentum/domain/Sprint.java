package com.momentum.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "SPRINT")
public class Sprint {
    @Id
    @Column(name = "SPRINT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PROJECT_ID",
            nullable = false)
    Project project;

    @OneToMany(mappedBy = "sprint")
    List<Card> cards = new ArrayList<>();

    @Column(name = "SPRINT_NAME",
            nullable = false)
    String name;

    @Column(name = "START_DATE",
            nullable = false)
    LocalDateTime start_date;

    @Column(name = "END_DATE",
            nullable = false)
    LocalDateTime end_date;

    @Builder
    public Sprint(Project project, String name, LocalDateTime start_date, LocalDateTime end_date) {
        this.project = project;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;

        project.addSprint(this);
    }
    public Long update(String name, LocalDateTime start_date, LocalDateTime end_date) {
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;

        return this.id;
    }
    public void addCard(Card card) {
        this.cards.add(card);
    }
}
