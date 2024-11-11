package com.agilelens.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SprintCreateRequest {
    private Long project_id;
    private String name;
    private LocalDateTime start_date;
    private LocalDateTime end_date;

    @Builder
    public SprintCreateRequest(Long project_id, String name, LocalDateTime start_date, LocalDateTime end_date) {
        this.project_id = project_id;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
