package com.example.easytourneybe.match;

import com.example.easytourneybe.enums.match.TypeMatch;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.match.dto.EventCreateAndUpdateDto;
import com.example.easytourneybe.match.dto.MatchDto;
import com.example.easytourneybe.match.interfaces.EventService;
import com.example.easytourneybe.match.interfaces.IMatchRepository;
import com.example.easytourneybe.util.MatchUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventDateService eventDateService;

    private final IMatchRepository matchRepository;

    private final MatchUtils matchUtils;
    @Override
    public MatchDto createEvent(Integer eventDateId, EventCreateAndUpdateDto evtCreateDto) {
        Integer timeDuration = evtCreateDto.getTimeDuration();
        if (timeDuration <= 0 || timeDuration >=24*60)
            throw new InvalidRequestException("Time duration not valid.");

        Optional<EventDate> eventDateOpt = eventDateService.findByEventDateId(eventDateId);
        if (eventDateOpt.isEmpty())
            throw new NoSuchElementException("Not found Event Date with Id: " + eventDateId);
        EventDate evtDate = eventDateOpt.get();
        if (evtDate.getDate().isBefore(LocalDate.now()))
            throw new InvalidRequestException("Can not add new Event into Event Date in the past: " + evtDate.getDate());

        LocalTime startTime = eventDateOpt.get().getStartTime();
        List<Match> matches = matchRepository.findAllByEventDateId(eventDateId);
        if (!matches.isEmpty()) {
            for (Match match : matches) {
                if (match.getEndTime().isAfter(startTime))
                    startTime = match.getEndTime();
            }
        }
        if (evtDate.getDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(startTime))
            startTime = LocalTime.now();

        LocalTime endTime = startTime.plusMinutes(evtCreateDto.getTimeDuration());
        if (endTime.compareTo(startTime) <= 0)
            throw new InvalidRequestException("Not enough time to schedule");

        Match newEvent = Match.builder()
                .eventDateId(eventDateId)
                .startTime(startTime)
                .endTime(endTime)
                .title(evtCreateDto.getTitle())
                .type(TypeMatch.EVENT)
                .matchDuration(evtCreateDto.getTimeDuration())
                .build();
        newEvent = matchRepository.save(newEvent);
        return matchUtils.convertMatchtoMatchDTO(newEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Integer eventId) {
        Match event = checkEventValidity(eventId);

        EventDate eventDate = eventDateService.findByEventDateId(event.getEventDateId()).get();

        LocalTime startTime = event.getStartTime();
        Integer duration = event.getMatchDuration();
        if (eventDate.getDate().compareTo(LocalDate.now()) <= 0 && LocalTime.now().isAfter(startTime))
            throw new InvalidRequestException("Can not delete Event in the past or in processing");

        matchRepository.delete(event);
        List<Match> matches = matchRepository.findAllByEventDateId(event.getEventDateId());
        for (Match match : matches) {
            if (match.getStartTime().isAfter(startTime)) {
                match.setStartTime(match.getStartTime().minusMinutes(duration));
                match.setEndTime(match.getEndTime().minusMinutes(duration));
            }
        }
        matchRepository.saveAll(matches);
    }

    @Override
    @Transactional
    public List<MatchDto> updateEvent(Integer eventId, EventCreateAndUpdateDto eventDto) {
        Match event = checkEventValidity(eventId);
        EventDate eventDate = eventDateService.findByEventDateId(event.getEventDateId()).get();

        LocalTime startTime = event.getStartTime();
        if (eventDate.getDate().compareTo(LocalDate.now()) <= 0 && LocalTime.now().isAfter(startTime))
            throw new InvalidRequestException("Can not update Event in the past or in processing");

        final Integer oldDuration = event.getMatchDuration();
        final Integer duration = eventDto.getTimeDuration();
        event.setTitle(eventDto.getTitle());
        event.setMatchDuration(duration);
        event.setEndTime(event.getStartTime().plusMinutes(duration));
        final int increaseTime = duration - oldDuration;
        matchRepository.save(event);

        List<Match> matches = matchRepository.findAllByEventDateId(event.getEventDateId());
        for (Match match : matches) {
            if (match.getStartTime().isAfter(startTime)) {
                LocalTime start = match.getStartTime();
                LocalTime end = match.getEndTime();
                if (end.plusMinutes(increaseTime).isBefore(end) && increaseTime > 0) {
                    throw new InvalidRequestException("Not enough time to schedule");
                }
                match.setStartTime(start.plusMinutes(increaseTime));
                match.setEndTime(match.getEndTime().plusMinutes(increaseTime));
            }
        }
        matches = matchRepository.saveAll(matches);
        return matchUtils.convertMatchToMatchDto(matches);
    }

    public Match checkEventValidity(Integer eventId) {
        Optional<Match> eventOpt = matchRepository.findById(eventId);
        if (eventOpt.isEmpty())
            throw new NoSuchElementException("Not found Event with Id: " + eventId);
        Match event = eventOpt.get();
        if (!event.getType().equals(TypeMatch.EVENT))
            throw new NoSuchElementException("Not found Event with Id: " + eventId);
        return event;
    }
}
