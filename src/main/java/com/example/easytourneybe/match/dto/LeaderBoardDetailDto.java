package com.example.easytourneybe.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderBoardDetailDto {
    List<LeaderBoardDto> leaderBoard;
    List<MatchOfLeaderBoardDto> matches;
}
