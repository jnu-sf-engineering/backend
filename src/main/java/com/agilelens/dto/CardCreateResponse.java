package com.agilelens.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CardCreateResponse {
    private Long card_id;

    @Builder
    public CardCreateResponse(Long card_id) {
        this.card_id = card_id;
    }

    public static CardCreateResponse from(Long card_id) {
        return CardCreateResponse.builder().card_id(card_id).build();
    }
}
