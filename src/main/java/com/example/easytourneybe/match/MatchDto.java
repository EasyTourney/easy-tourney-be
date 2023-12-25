package com.example.easytourneybe.match;

import com.example.easytourneybe.enums.match.TypeMatch;
import com.example.easytourneybe.team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {
    private Long id;
    private Team teamOne;
    private Team teamTwo;
    private Integer teamOneResult;
    private Integer teamTwoResult;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer eventDateId;
    private String title;
    private TypeMatch type;
}
