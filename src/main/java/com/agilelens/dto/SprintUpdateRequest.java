package com.agilelens.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SprintUpdateRequest {
    private String name;
    private LocalDateTime start_date;
    private LocalDateTime end_date;

    @Builder
    public SprintUpdateRequest(String name, LocalDateTime start_date, LocalDateTime end_date) {
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
