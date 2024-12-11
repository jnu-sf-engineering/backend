package com.momentum.dto;

import com.momentum.domain.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class CardCreateRequest {
    @NotNull(message="sprint_id")
    private Long sprint_id;

    @NotEmpty(message="content")
    private String content;

    @NotEmpty(message="participants")
    private Set<String> participants;

    @NotNull(message="status")
    private Status status;

    @Builder
    public CardCreateRequest(String content, Status status, Set<String> participants, Long sprint_id) {
        this.content = content;
        this.status = status;
        this.participants = participants;
        this.sprint_id = sprint_id;
    }
}
