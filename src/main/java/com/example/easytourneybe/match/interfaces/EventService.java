package com.example.easytourneybe.match.interfaces;

import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.dto.MatchDto;
import com.example.easytourneybe.match.dto.EventCreateAndUpdateDto;

import java.util.List;

public interface EventService {

    GenerationDto createEvent(Integer eventDateId, EventCreateAndUpdateDto evtCreateDto);

    GenerationDto deleteEvent(Integer eventId);

    GenerationDto updateEvent(Integer eventId, EventCreateAndUpdateDto eventDto);
}
