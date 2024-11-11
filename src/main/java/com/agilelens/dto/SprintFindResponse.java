package com.agilelens.dto;

import com.agilelens.domain.Card;
import com.agilelens.domain.Sprint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
public class SprintFindResponse {
    private Long sprint_id;
    private String name;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private Cards cards;

    @Builder
    private SprintFindResponse(Long sprint_id, String name, LocalDateTime start_date, LocalDateTime end_date, Cards cards) {
        this.sprint_id = sprint_id;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.cards = cards;
    }

    public static SprintFindResponse from(final Sprint sprint) {
        Cards cards = new Cards();

        List<Card> cardList = sprint.getCards();
        for (Card card : cardList) {
            switch (card.getStatus()) {
                case TO_DO -> cards.addToDo(card);
                case IN_PROGRESS -> cards.addInProgress(card);
                case DONE -> cards.addDone(card);
            }
        }

        return SprintFindResponse.builder().sprint_id(sprint.getId()).name(sprint.getName())
                .start_date(sprint.getStart_date()).end_date(sprint.getEnd_date()).cards(cards).build();
    }

    @NoArgsConstructor
    @Getter
    static class Cards {
        private List<Card_> to_do = new ArrayList<>();
        private List<Card_> in_progress = new ArrayList<>();
        private List<Card_> done = new ArrayList<>();

        public void addToDo(Card card) {
            addCard_(to_do, card);
        }
        public void addInProgress(Card card) {
            addCard_(in_progress, card);
        }

        public void addDone(Card card) {
            addCard_(done, card);
        }

        public void addCard_(List<Card_> card_List, Card card) {
            card_List.add(Card_.builder().card_id(card.getId()).content(card.getContent()).participants(card.getParticipants())
                    .build());
        }
    }

    @NoArgsConstructor
    @Getter
    static class Card_ {
        private Long card_id;
        private String content;
        private Set<String> participants;

        @Builder
        public Card_(Long card_id, String content, Set<String> participants) {
            this.card_id = card_id;
            this.content = content;
            this.participants = participants;
        }
    }
}
