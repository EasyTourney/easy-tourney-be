package com.example.easytourneybe.match;

import com.example.easytourneybe.enums.match.TypeMatch;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.dto.EventCreateAndUpdateDto;
import com.example.easytourneybe.match.interfaces.EventService;
import com.example.easytourneybe.match.interfaces.IMatchRepository;
import com.example.easytourneybe.tournament.Tournament;
import com.example.easytourneybe.tournament.TournamentService;
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

    private final TournamentService tournamentService;
    @Override
    public GenerationDto createEvent(Integer eventDateId, EventCreateAndUpdateDto evtCreateDto, Integer tournamentId) {
        Tournament tournament = tournamentService.findById(tournamentId).get();
        Optional<EventDate> eventDateOpt = eventDateService.findByEventDateId(eventDateId);
        if (eventDateOpt.isEmpty())
            throw new NoSuchElementException("Not found Event Date with Id: " + eventDateId);
        EventDate evtDate = eventDateOpt.get();
        if (evtDate.getDate().isBefore(LocalDate.now()))
            throw new InvalidRequestException("Can not add new Event into Event Date in the past: " + evtDate.getDate());
        if (evtDate.getDate().isEqual(LocalDate.now()) && evtDate.getStartTime().isBefore(LocalTime.now()))
            throw new InvalidRequestException("Can not add new Event into Event Date has already start");

        if (!evtDate.getTournamentId().equals(tournamentId))
            throw new InvalidRequestException("Can not create Event in another Tournament");
        LocalTime startTime = eventDateOpt.get().getStartTime();
        int timeBetween = tournamentService.findById(evtDate.getTournamentId()).get().getTimeBetween();
        List<Match> matches = matchRepository.findAllByEventDateId(eventDateId);
        if (!matches.isEmpty()) {
            for (Match match : matches) {
                if (match.getEndTime().isAfter(startTime))
                    startTime = match.getEndTime().plusMinutes(timeBetween);
            }
        }

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

        TournamentStatus status = tournament.getStatus();
        if (status.equals(TournamentStatus.FINISHED))
            throw new InvalidRequestException("Can not create or edit or delete Event in tournament has been Finished");
        if (status.equals(TournamentStatus.DELETED))
            throw new InvalidRequestException("Can not create or edit or delete Event in tournament has been Deleted");
        if (status.equals(TournamentStatus.DISCARDED))
            throw new InvalidRequestException("Can not create or edit or delete Event in tournament has been Discarded");

        newEvent = matchRepository.save(newEvent);
        matches = matchRepository.findAllByEventDateId(eventDateId);
        return GenerationDto.builder()
                .eventDateId(eventDateId)
                .matches(matchUtils.convertMatchToMatchDto(matches))
                .date(evtDate.getDate())
                .startTime(evtDate.getStartTime())
                .endTime(evtDate.getEndTime())
                .build();

    }

    @Override
    @Transactional
    public GenerationDto deleteEvent(Integer eventId, Integer tournamentId) {
        Match event = checkEventValidity(eventId, tournamentId);

        EventDate eventDate = eventDateService.findByEventDateId(event.getEventDateId()).get();

        LocalTime startTime = event.getStartTime();
        Integer duration = event.getMatchDuration();
        int betweenTime = tournamentService.findById(eventDate.getTournamentId()).get().getTimeBetween();
        int timeToMinus = duration + betweenTime;
        if (eventDate.getDate().compareTo(LocalDate.now()) <= 0 && LocalTime.now().isAfter(startTime))
            throw new InvalidRequestException("Can not delete Event in the past or in processing");

        matchRepository.delete(event);
        List<Match> matches = matchRepository.findAllByEventDateId(event.getEventDateId());
        for (Match match : matches) {
            if (match.getStartTime().isAfter(startTime)) {
                match.setStartTime(match.getStartTime().minusMinutes(timeToMinus));
                match.setEndTime(match.getEndTime().minusMinutes(timeToMinus));
            }
        }
        matchRepository.saveAll(matches);
        return GenerationDto.builder()
                .eventDateId(eventDate.getId())
                .matches(matchUtils.convertMatchToMatchDto(matches))
                .date(eventDate.getDate())
                .startTime(eventDate.getStartTime())
                .endTime(eventDate.getEndTime())
                .build();
    }

    @Override
    @Transactional
    public GenerationDto updateEvent(Integer eventId, EventCreateAndUpdateDto eventDto, Integer tournamentId) {

        Match event = checkEventValidity(eventId, tournamentId);
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

        return GenerationDto.builder()
                .eventDateId(eventDate.getId())
                .matches(matchUtils.convertMatchToMatchDto(matches))
                .date(eventDate.getDate())
                .startTime(eventDate.getStartTime())
                .endTime(eventDate.getEndTime())
                .build();
    }

    public Match checkEventValidity(Integer eventId, Integer tournamentId) {
        Tournament tournament = tournamentService.findById(tournamentId).get();
        Optional<Match> eventOpt = matchRepository.findById(eventId);
        if (eventOpt.isEmpty())
            throw new NoSuchElementException("Not found Event with Id: " + eventId);
        Match event = eventOpt.get();
        if (!event.getType().equals(TypeMatch.EVENT))
            throw new NoSuchElementException("Not found Event with Id: " + eventId);
        EventDate eventDate = eventDateService.findByEventDateId(event.getEventDateId()).get();
        if (!eventDate.getTournamentId().equals(tournamentId))
            throw new InvalidRequestException("Can not change Event in another Tournament");
        TournamentStatus status = tournament.getStatus();
        if (status.equals(TournamentStatus.FINISHED))
            throw new InvalidRequestException("Can not create or edit or delete Event in tournament has been Finished");
        if (status.equals(TournamentStatus.DELETED))
            throw new InvalidRequestException("Can not create or edit or delete Event in tournament has been Deleted");
        if (status.equals(TournamentStatus.DISCARDED))
            throw new InvalidRequestException("Can not create or edit or delete Event in tournament has been Discarded");
        return event;
    }
}
