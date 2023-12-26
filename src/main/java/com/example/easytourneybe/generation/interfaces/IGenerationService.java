package com.example.easytourneybe.generation.interfaces;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.MatchDto;

import java.time.LocalTime;
import java.util.List;

public interface IGenerationService {

    List<GenerationDto> generate(Integer tournamentId, Integer duration, Integer betweenTime, List<EventDate> eventDates);

    List<GenerationDto> updateGeneration(MatchDto matchDto, Integer eventDateId, LocalTime startTime, LocalTime endTime);
}
