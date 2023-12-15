package com.example.easytourneybe.team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPlayerDto {
    private Long team_id;
    private String team_name;
    private Long player_count;
}
