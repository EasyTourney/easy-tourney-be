package com.example.easytourneybe.generation;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.generation.interfaces.IGenerationService;
import com.example.easytourneybe.match.Match;
import com.example.easytourneybe.match.dto.MatchDto;
import com.example.easytourneybe.match.interfaces.IMatchRepository;
import com.example.easytourneybe.match.interfaces.IMatchService;
import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.team.Team;
import com.example.easytourneybe.tournament.Tournament;
import com.example.easytourneybe.tournament.TournamentRepository;
import com.example.easytourneybe.tournament.TournamentService;
import com.example.easytourneybe.util.MatchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class GenerationService implements IGenerationService {
    @Autowired
    private IMatchService matchService;
    @Autowired
    private EventDateService eventDateService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private MatchUtils matchUtils;

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private IMatchRepository matchRepository;
    @Override
    public List<GenerationDto> generate(Integer tournamentId, Integer duration, Integer betweenTime, LocalTime startTime, LocalTime endTime) {
        List<List<Team>> matches = matchService.matchList(tournamentId);
        List<EventDate> eventDates = eventDateService.findAllByTournamentId(tournamentId);
        Optional<Tournament> tournament = tournamentService.findById(tournamentId);
        if (tournament.isPresent()) {
            if (duration == null) {
                duration = tournament.get().getMatchDuration();
            } else {
                tournament.get().setMatchDuration(duration);
            }
            if (betweenTime == null) {
                betweenTime = tournament.get().getTimeBetween();
            } else {
                tournament.get().setTimeBetween(betweenTime);
            }
            tournamentRepository.save(tournament.get());
        }

        //If there are changes , update its value.
        if (startTime != null) {
            eventDates.forEach(eventDate -> eventDate.setStartTime(startTime));
        }
        if (endTime != null) {
            eventDates.forEach(eventDate -> eventDate.setEndTime(endTime));
        }

        if (eventDates.size() == 0) throw new InvalidRequestException("Event date is empty, please add them.");
        eventDateService.saveAll(eventDates);

        List<GenerationDto> generations = new ArrayList<>();

        Map<EventDate, List<List<LocalTime>>> timeSheetMatches = matchService.timeSheetMatches(duration, betweenTime, matches.size(), eventDates);
        if (!matchUtils.compareNumMatchAndNumMatchTime(matches.size(), matchUtils.numberMatchTimes(timeSheetMatches))) {
            throw new InvalidRequestException("The tournament schedule does not accommodate the current number of matches.");
        } else {
            List<MatchDto> matchList = matchService.mappingMatchAndTime(matches, timeSheetMatches,duration);
            eventDates.sort(Comparator.comparing(EventDate::getDate));
            for (EventDate eventDate : eventDates) {
                generations.add(matchUtils.createGeneration(Optional.ofNullable(eventDate), matchList));
            }
            return generations;
        }
    }

    @Override
    public List<GenerationDto> updateGeneration(Long matchId, Integer eventDateIdSelected, Long newPositionMatchId) {
        List<GenerationDto> generations = new ArrayList<>();

        //get Match by matchDtoId from DB
        Match oldMatch = matchService.getMatchById(matchId);
        Match matchOfNewTime = matchService.getMatchById(newPositionMatchId);
        Match match = (Match) oldMatch.clone();

        //get new and old event date objects
        Optional<EventDate> oldEventDate = eventDateService.findByEventDateId(oldMatch.getEventDateId());
        Optional<EventDate> newEventDate = eventDateService.findByEventDateId(eventDateIdSelected);

        //cannot switch to a date in the past
        if (newEventDate.get().getDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Cannot switch to a date in the past.");
        }

        //get all match of event date new and event date old
        List<Match> matchesOfNewEventDate = matchService.getMatchByEventDateId(eventDateIdSelected);
        Integer indexOfNewTime = null;
        if (matchOfNewTime != null) {
            match.setStartTime(matchOfNewTime.getStartTime());
            match.setEndTime(matchOfNewTime.getEndTime());
            match.setEventDateId(eventDateIdSelected);
            indexOfNewTime = matchesOfNewEventDate.indexOf(matchOfNewTime);
        }

        //get tournament to get between time and match duration
        Optional<Tournament> tournament = tournamentService.findById(newEventDate.get().getTournamentId());
        int betweenTime = tournament.get().getTimeBetween();
        int duration = tournament.get().getMatchDuration();
        LocalTime startTime = newEventDate.get().getStartTime();
        List<List<Match>> matchesUpdated = new ArrayList<>();

        //Check for changes within a day or between two different days.
        if (Objects.equals(oldMatch.getEventDateId(), eventDateIdSelected)) {
            matchService.saveAll(updateInDate(match, duration, betweenTime, matchesOfNewEventDate, oldMatch, indexOfNewTime, matchOfNewTime));
        } else {
            matchesUpdated = updateTwoDifferentDays(match, duration, betweenTime, matchesOfNewEventDate, oldMatch, indexOfNewTime, matchOfNewTime, startTime, eventDateIdSelected);
            matchService.saveAll(matchesUpdated.get(1));
            List<MatchDto> matchDTOsOfOldEventDate = matchUtils.convertMatchListToMatchDtoList(matchService.getMatchByEventDateId(oldEventDate.get().getId()));
            generations.add(matchUtils.createGeneration(oldEventDate, matchDTOsOfOldEventDate));
            matchService.saveAll(matchesUpdated.get(0));
        }


        List<MatchDto> matchDTOsOfNewEventDate = matchUtils.convertMatchListToMatchDtoList(matchService.getMatchByEventDateId(eventDateIdSelected));
        generations.add(matchUtils.createGeneration(newEventDate, matchDTOsOfNewEventDate));
        return generations;
    }

    @Override
    public ResponseEntity<ResponseObject> getAllGeneration(Integer tournamentId) {
        List<EventDate> eventDates = eventDateService.findAllByTournamentId(tournamentId);
        List<GenerationDto> generations = new ArrayList<>();
        eventDates.sort(Comparator.comparing(EventDate::getDate));
        for (EventDate eventDate : eventDates) {

            List<MatchDto> matches = matchUtils.convertMatchListToMatchDtoList(matchService.getMatchByEventDateId(eventDate.getId()));
            generations.add(matchUtils.createGeneration(Optional.of(eventDate), matches));
        }
        ResponseObject responseObject = new ResponseObject(
                true, generations.size(), generations
        );

        List<Match> duplicateMatch = matchRepository.findAllDuplicateMatchByTournamentId(tournamentId);
        //if there are a duplicate match, then warning
        if (duplicateMatch.size() > 1) {
            responseObject.setAdditionalData(java.util.Collections.singletonMap("duplicateMatch", duplicateMatch));
        }
        //if the time of event date is not enough for all matches, then warning
        if(!checkEnoughTime(eventDates).isEmpty()){
            responseObject.setAdditionalData(java.util.Collections.singletonMap("Time not enough", checkEnoughTime(eventDates)));
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    public List<Match> updateInDate(Match match, Integer duration, Integer betweenTime, List<Match> matchesOfNewEventDate, Match oldMatch, Integer indexOfNewTime, Match matchOfNewTime) {
        //Changes within a day.
        LocalTime endTime;
        List<Match> matchesNew = new ArrayList<>();
        //Find the position of oldMatch in list.
        int indexOfOldMatch = matchesOfNewEventDate.indexOf(oldMatch);
        //remove oldMatch from list
        matchesOfNewEventDate.remove(oldMatch);
        //Adjust the timing of matches after the changes.
        if (indexOfOldMatch < indexOfNewTime) {
            endTime = oldMatch.getStartTime().minusMinutes(betweenTime);
            matchesNew = updateTime(endTime, betweenTime, duration, matchesOfNewEventDate.subList(indexOfOldMatch, indexOfNewTime).size() + indexOfOldMatch, matchesOfNewEventDate, indexOfOldMatch);
        } else if (indexOfNewTime < indexOfOldMatch) {
            endTime = matchOfNewTime.getEndTime();
            matchesNew = updateTime(endTime, betweenTime, duration, matchesOfNewEventDate.subList(indexOfNewTime, indexOfOldMatch).size() + indexOfNewTime, matchesOfNewEventDate, indexOfNewTime);
        }
        matchesNew.add(indexOfNewTime, match);

        return matchesNew;
    }


    public List<List<Match>> updateTwoDifferentDays(Match match, Integer duration, Integer betweenTime,
                                                    List<Match> matchesOfNewEventDate, Match oldMatch, Integer indexOfNewTime, Match matchOfNewTime, LocalTime startTime,
                                                    Integer eventDateIdSelected) {
        //change the time between two different days.
        List<Match> matchesOfOldEventDate = matchService.getMatchByEventDateId(oldMatch.getEventDateId());

        int matchesNewEventDateSize = matchesOfNewEventDate.size();
        int matchesOldEventDateSize = matchesOfOldEventDate.size();

        List<Match> matchesNew = new ArrayList<>();
        List<Match> matchesOld;
        List<List<Match>> matchesUpdated = new ArrayList<>();

        int indexOfOldTime = matchesOfOldEventDate.indexOf(oldMatch);

        //update matches in new event date
        //In case of changing the time slot beyond the last element of the array.
        if (matchOfNewTime == null) {
            //if the match is moved to an event date where no matches have taken place yet
            if (matchesNewEventDateSize == 0) {
                match.setStartTime(startTime);
                match.setEndTime(startTime.plusMinutes(duration));
                match.setEventDateId(eventDateIdSelected);
                matchesNew.add(match);
            } else {
                //If the match is changed to the last position of the event date
                LocalTime newStartTime = matchesOfNewEventDate.get(matchesNewEventDateSize - 1).getEndTime().plusMinutes(betweenTime);
                match.setStartTime(newStartTime);
                match.setEndTime(newStartTime.plusMinutes(duration));
                match.setEventDateId(matchesOfNewEventDate.get(matchesNewEventDateSize - 1).getEventDateId());
                matchesNew.addAll(matchesOfNewEventDate);
                matchesNew.add(match);
            }

        } else {
            matchesNew = updateTime(matchOfNewTime.getEndTime(), betweenTime, duration, matchesNewEventDateSize, matchesOfNewEventDate, indexOfNewTime);
            matchesNew.add(indexOfNewTime, match);
        }

        matchesOfOldEventDate.remove(oldMatch);
        LocalTime endTime = oldMatch.getStartTime().minusMinutes(betweenTime);
        matchesOld = updateTime(endTime, betweenTime, duration, matchesOldEventDateSize - 1, matchesOfOldEventDate, indexOfOldTime);

        matchesUpdated.add(0, matchesNew);
        matchesUpdated.add(1, matchesOld);

        return matchesUpdated;
    }


    //Update the time when modifying the schedule of a match.
    private List<Match> updateTime(LocalTime endTime, Integer betweenTime, Integer duration, Integer matchesSize, List<Match> matchList, int index) {
        LocalTime start = endTime.plusMinutes(betweenTime);
        LocalTime end = start.plusMinutes(duration);
        for (int j = index; j < matchesSize; j++) {
            matchList.get(j).setStartTime(start);
            matchList.get(j).setEndTime(end);
            start = end.plusMinutes(betweenTime);
            end = start.plusMinutes(duration);
        }
        return matchList;
    }
    private Map<String, Object> checkEnoughTime(List<EventDate> eventDates) {
        String warningMessage = "";
        List<Integer> eventDateId = new ArrayList<>();

        for (EventDate eventDate : eventDates) {
            List<Match> matches = matchService.getMatchByEventDateId(eventDate.getId());

            for (Match match : matches) {
                if (match.getStartTime().isAfter(eventDate.getEndTime()) || match.getEndTime().isAfter(eventDate.getEndTime())) {
                    warningMessage = "Time of event date is not enough for all matches";
                    eventDateId.add(eventDate.getId());
                    break;
                }
            }
        }
        if(warningMessage.isEmpty()){
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("warningMessage", warningMessage);
        result.put("eventDateId", eventDateId);
        return result;
    }


}
