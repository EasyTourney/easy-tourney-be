package com.example.easytourneybe.team.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPlayerDto {
    private Long teamId;
    private String teamName;
    private Long playerCount;
}