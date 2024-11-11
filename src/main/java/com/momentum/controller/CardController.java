package com.momentum.controller;

import com.momentum.domain.Card;
import com.momentum.domain.Status;
import com.momentum.dto.CardCreateRequest;
import com.momentum.dto.CardCreateResponse;
import com.momentum.dto.CardMoveResponse;
import com.momentum.dto.CardUpdateRequest;
import com.momentum.global.CommonResponse;
import com.momentum.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1/cards")
@RestController
public class CardController {
    private final CardService cardService;

    @PostMapping()
    public CommonResponse<CardCreateResponse> create(@RequestBody CardCreateRequest request) {
        CardCreateResponse response = CardCreateResponse
                .from(cardService.save(request));
        return CommonResponse.success(response);
    }

    @PutMapping("/{card_id}")
    public CommonResponse<CardCreateResponse> update(@PathVariable("card_id") Long card_id,
                                       @RequestBody CardUpdateRequest request) {
        CardCreateResponse response = CardCreateResponse
                .from(cardService.update(card_id, request));
        return CommonResponse.success(response);
    }

    @DeleteMapping("/{card_id}")
    public CommonResponse<Void> delete(@PathVariable("card_id") Long card_id) {
        cardService.delete(card_id);
        return CommonResponse.success();
    }

    @PostMapping("/{card_id}/move")
    public CommonResponse<CardMoveResponse> move(@PathVariable("card_id") Long card_id,
                                                 @RequestBody Status status) {
        Card card = cardService.findById(card_id);
        Status prev_status = card.getStatus();

        cardService.move(card_id, status);
        CardMoveResponse response = CardMoveResponse.from(card, prev_status);
        return CommonResponse.success(response);
    }
}
