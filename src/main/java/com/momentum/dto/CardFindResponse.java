package com.momentum.dto;

import com.momentum.domain.Card;
import com.momentum.domain.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class CardFindResponse {
    private Long card_id;
    private String content;
    private Set<String> participants;
    private Status status;

    @Builder
    private CardFindResponse(Long card_id, String content, Set<String> participants, Status status) {
        this.card_id = card_id;
        this.content = content;
        this.participants = participants;
        this.status = status;
    }

    public static CardFindResponse from(final Card card) {
        return CardFindResponse.builder().card_id(card.getId()).content(card.getContent())
                .participants(card.getParticipants()).status(card.getStatus()).build();
    }
}
