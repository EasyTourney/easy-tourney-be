package com.example.easytourneybe.match.dto;

import com.example.easytourneybe.enums.match.TypeMatch;
import com.example.easytourneybe.team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MatchResultDto {
    private Long id;
    private Long teamOneId;
    private String teamOneName;
    private Integer teamOneResult;
    private Long teamTwoId;
    private String teamTwoName;
    private Integer teamTwoResult;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer eventDateId;
    private String title;
    private TypeMatch type;
}