package com.example.easytourneybe.generation.interfaces;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.generation.GenerationDto;

import java.time.LocalTime;
import java.util.List;

public interface IGenerationService {

    List<GenerationDto> generate(Integer tournamentId, Integer duration, Integer betweenTime, LocalTime startTime,LocalTime endTime);

    List<GenerationDto> updateGeneration(Long matchId, Integer eventDateId,Long newPositionMatchId);

    List<GenerationDto> getAllGeneration(Integer tournamentId);
}
