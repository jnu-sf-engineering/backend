package com.momentum.service;

import com.momentum.domain.Card;
import com.momentum.domain.Status;
import com.momentum.dto.CardCreateRequest;
import com.momentum.dto.CardUpdateRequest;
import com.momentum.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CardService {
    private final CardRepository cardRepository;
    private final SprintService sprintService;

    @Transactional
    public Long save(CardCreateRequest request) {
        Card card = Card.builder()
                .content(request.getContent())
                .participants(request.getParticipants())
                .status(request.getStatus())
                .sprint(sprintService.findById(request.getSprint_id()))
                .build();

        cardRepository.save(card);
        return card.getId();
    }
    @Transactional
    public Long update(Long id, CardUpdateRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("해당 카드가 없습니다. id=" + id)));

        return card.update(request.getContent(), request.getParticipants(), request.getStatus());
    }
    @Transactional

    public void delete(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("해당 카드가 없습니다. id=" + id)));

        cardRepository.delete(card);
    }
    @Transactional
    public void move(Long id, Status status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("해당 카드가 없습니다. id=" + id)));
        card.move(status);
    }

    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("해당 카드가 없습니다. id=" + id)));
    }
}

