package com.example.easytourneybe.match.interfaces;

import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.dto.EventCreateAndUpdateDto;

public interface EventService {

    GenerationDto createEvent(Integer eventDateId, EventCreateAndUpdateDto evtCreateDto, Integer tournamentId);

    GenerationDto deleteEvent(Integer eventId, Integer tournamentId);

    GenerationDto updateEvent(Integer eventId, EventCreateAndUpdateDto eventDto, Integer tournamentId);
}
