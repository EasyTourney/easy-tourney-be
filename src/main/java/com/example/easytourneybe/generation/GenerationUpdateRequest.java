package com.example.easytourneybe.generation;

import com.example.easytourneybe.match.MatchDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationUpdateRequest {
    private Integer eventDateIdSelected;
    private LocalTime startTime;
    private LocalTime endTime;
    private MatchDto matchDto;
}
