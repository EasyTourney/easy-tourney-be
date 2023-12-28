package com.example.easytourneybe.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class MatchOfLeaderBoardDto {
    private Long matchId;

    private Long teamOneId;

    private String teamOneName;

    private Long teamTwoId;

    private String teamTwoName;

    private Integer teamOneResult;

    private Integer teamTwoResult;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Long teamWinId;
}
