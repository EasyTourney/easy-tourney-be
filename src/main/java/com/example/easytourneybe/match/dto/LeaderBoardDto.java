package com.example.easytourneybe.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LeaderBoardDto {
    private Long teamId;

    private String teamName;

    private Integer score;

    private Long theDifference;

    private Long totalResult;

    private Long rank;
}
