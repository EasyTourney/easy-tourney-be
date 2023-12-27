package com.example.easytourneybe.match.interfaces;

import com.example.easytourneybe.match.MatchDto;
import com.example.easytourneybe.match.dto.EventCreateAndUpdateDto;

import java.util.List;

public interface EventService {
    MatchDto createEvent(Integer eventDateId, EventCreateAndUpdateDto evtCreateDto);

    void deleteEvent(Integer eventId);

    List<MatchDto> updateEvent(Integer eventId, EventCreateAndUpdateDto eventDto);
}
