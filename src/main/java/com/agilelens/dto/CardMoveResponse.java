package com.agilelens.dto;

import com.agilelens.domain.Card;
import com.agilelens.domain.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CardMoveResponse {
    private Long card_id;
    private Status prev_status;
    private Status status;

    @Builder
    private CardMoveResponse(Long card_id, Status prev_status, Status status) {
        this.card_id = card_id;
        this.prev_status = prev_status;
        this.status = status;
    }

    public static CardMoveResponse from(Card card, Status prev_status) {
        return CardMoveResponse.builder().card_id(card.getId()).prev_status(prev_status).status(card.getStatus())
                .build();
    }
}
