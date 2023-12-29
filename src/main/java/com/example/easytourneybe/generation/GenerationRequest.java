package com.example.easytourneybe.generation;

import com.example.easytourneybe.eventdate.dto.EventDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationRequest {
    private Integer duration;
    private Integer betweenTime;
    private LocalTime startTime;
    private LocalTime endTime;

}
