package com.example.easytourneybe.generation;

import com.example.easytourneybe.match.MatchDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationDto {
    private Integer eventDateId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private List<MatchDto> matches;
}
