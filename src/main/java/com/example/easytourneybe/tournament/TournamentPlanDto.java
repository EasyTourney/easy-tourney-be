package com.example.easytourneybe.tournament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class TournamentPlanDto {
    private LocalTime startTimeDefault;
    private LocalTime endTimeDefault;
    private Integer timeBetween;
    private Integer matchDuration;
}
