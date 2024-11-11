package com.momentum.domain;

import com.momentum.global.SetToStringConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "CARD")
public class Card {
    @Id
    @Column(name = "CARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="SPRINT_ID",
            nullable = false)
    private Sprint sprint;

    @Column(name = "CARD_CONTENT",
            length = 255, nullable = false)
    private String content;

    @Convert(converter = SetToStringConverter.class)
    @Column(name = "CARD_PARTICIPANTS",
            nullable = false)
    private Set<String> participants;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "CARD_STATUS",
            nullable = false, columnDefinition = "TINYINT")
    private Status status;

    @Builder
    public Card( Sprint sprint, String content, Set<String> participants, Status status) {
        this.sprint = sprint;
        this.content = content;
        this.participants = participants;
        this.status = status;

        sprint.addCard(this);
    }
    public Long update(String content, Set<String> participants, Status status) {
        this.content = content;
        this.participants = participants;
        this.status = status;
        return this.id;
    }
    public void move(Status status) {
        this.status = status;
    }
}