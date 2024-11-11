package com.agilelens.dto;

import com.agilelens.domain.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Getter

public class CardUpdateRequest {
    private String content;
    private Status status;
    private Set<String> participants;

    @Builder
    public CardUpdateRequest(String content, Status status, Set<String> participants) {
        this.content = content;
        this.status = status;
        this.participants = participants;
    }
}
