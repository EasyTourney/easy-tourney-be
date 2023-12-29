package com.example.easytourneybe.generation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationUpdateRequest {
    private Integer eventDateIdSelected;
    private Long matchOfNewTimeId;
    private Long matchId;
}
