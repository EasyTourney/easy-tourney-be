package com.example.easytourneybe.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RequestDragDropMatch {
    private Integer matchId;
    private Integer newEventDateId;
    private Integer newIndexOfMatch;
}
