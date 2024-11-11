package com.momentum.dto;

import com.momentum.domain.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class CardCreateRequest {
    private Long sprint_id;
    private String content;
    private Status status;
    private Set<String> participants;

    @Builder
    public CardCreateRequest(String content, Status status, Set<String> participants, Long sprint_id) {
        this.content = content;
        this.status = status;
        this.participants = participants;
        this.sprint_id = sprint_id;
    }
}
