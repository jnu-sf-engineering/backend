package com.momentum.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SprintCreateRequest {
    @NotNull(message="project_id")
    private Long project_id;

    @NotEmpty(message="sprint_name")
    private String name;

    @NotNull(message="start_date")
    private LocalDateTime start_date;

    @NotNull(message="end_date")
    private LocalDateTime end_date;

    @Builder
    public SprintCreateRequest(Long project_id, String name, LocalDateTime start_date, LocalDateTime end_date) {
        this.project_id = project_id;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
