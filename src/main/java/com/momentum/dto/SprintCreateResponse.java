package com.momentum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SprintCreateResponse {
    private Long sprint_id;

    @Builder
    public SprintCreateResponse(Long sprint_id) {
        this.sprint_id = sprint_id;
    }

    public static SprintCreateResponse from(Long sprint_id) {
        return SprintCreateResponse.builder().sprint_id(sprint_id).build();
    }
}
