package com.example.easytourneybe.generation;

import com.example.easytourneybe.eventdate.EventDate;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.generation.interfaces.IGenerationService;
import com.example.easytourneybe.match.Match;
import com.example.easytourneybe.match.MatchDto;
import com.example.easytourneybe.match.interfaces.IMatchService;
import com.example.easytourneybe.team.Team;
import com.example.easytourneybe.tournament.Tournament;
import com.example.easytourneybe.tournament.TournamentService;
import com.example.easytourneybe.util.MatchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<GenerationDto> generate(Integer tournamentId, Integer duration, Integer betweenTime, List<EventDate> eventDateList) {
        List<List<Team>> matches = matchService.matchList(tournamentId);
        List<EventDate> eventDates;
        Optional<Tournament> tournament = tournamentService.findById(tournamentId);
        if (tournament.isPresent()) {
            if (duration == null) {
                duration = tournament.get().getMatchDuration();
            }
            if (betweenTime == null) {
                betweenTime = tournament.get().getTimeBetween();
            }
        }

        //If there are no changes in event dates from user, retrieve event dates from DB.
        if (eventDateList == null) {
            eventDates = eventDateService.findAllByTournamentId(tournamentId);
        } else {
            eventDates = new ArrayList<>(eventDateList);
        }
        if (eventDates.size() == 0) throw new InvalidRequestException("Event date is empty, please add them.");
        eventDateService.saveAll(eventDates);

        List<GenerationDto> generations = new ArrayList<>();

        Map<EventDate, List<List<LocalTime>>> timeSheetMatches = matchService.timeSheetMatches(duration, betweenTime, matches.size(), eventDates);
        if (!matchUtils.compareNumMatchAndNumMatchTime(matches.size(), matchUtils.numberMatchTimes(timeSheetMatches))) {
            throw new InvalidRequestException("The tournament schedule does not accommodate the current number of matches.");
        } else {
            List<MatchDto> matchList = matchService.mappingMatchAndTime(matches, timeSheetMatches);
            eventDates.sort(Comparator.comparing(EventDate::getDate));
            for (EventDate eventDate : eventDates) {
                generations.add(matchUtils.createGeneration(Optional.ofNullable(eventDate), matchList));
            }
            return generations;
        }
    }

    @Override
    public List<GenerationDto> updateGeneration(MatchDto matchDto, Integer eventDateIdSelected, LocalTime startTime, LocalTime endTime) {
        Match match = matchService.getMatchById(matchDto.getId());
        List<Match> matchesOfEventDateSelected = matchService.getMatchByEventDateId(eventDateIdSelected);
        List<Match> matchesOfEventDateChange = matchService.getMatchByEventDateId(match.getEventDateId());
        Optional<EventDate> eventDateChange = eventDateService.findByEventDateId(matchDto.getEventDateId());
        Optional<EventDate> eventDateSelected = eventDateService.findByEventDateId(eventDateIdSelected);
        Optional<Tournament> tournament = tournamentService.findById(eventDateSelected.get().getTournamentId());
        int matchesSelectedSize = matchesOfEventDateSelected.size();
        int matchesChangeSize = matchesOfEventDateChange.size();
        int betweenTime = tournament.get().getTimeBetween();
        int duration = tournament.get().getMatchDuration();

        for (int j = 0; j < matchesChangeSize; j++) {
            if (Objects.equals(matchesOfEventDateChange.get(j).getStartTime(), startTime) && Objects.equals(matchesOfEventDateChange.get(j).getEndTime(), endTime)) {
                updateTime(endTime, betweenTime, duration, matchesChangeSize, matchesOfEventDateChange, j);
                break;
            }
        }


        for (int i = 0; i < matchesSelectedSize; i++) {
            if (Objects.equals(matchesOfEventDateSelected.get(i).getStartTime(), startTime) && Objects.equals(matchesOfEventDateSelected.get(i).getEndTime(), endTime)) {
                match.setStartTime(startTime);
                match.setEndTime(endTime);
                match.setEventDateId(eventDateIdSelected);
                updateTime(endTime, betweenTime, duration, matchesSelectedSize, matchesOfEventDateSelected, i);
                matchService.updateMatch(match);
                break;
            }
        }


        List<MatchDto> matchDTOsOfEventDateWillChange = matchUtils.convertMatchListToMatchDtoList(matchService.getMatchByEventDateId(matchDto.getEventDateId()));
        List<MatchDto> matchDTOsOfEventDateAfterUpdated = matchUtils.convertMatchListToMatchDtoList(matchService.getMatchByEventDateId(eventDateIdSelected));

        List<GenerationDto> generations = new ArrayList<>();
        generations.add(matchUtils.createGeneration(eventDateSelected, matchDTOsOfEventDateAfterUpdated));
        generations.add(matchUtils.createGeneration(eventDateChange, matchDTOsOfEventDateWillChange));

        return generations;
    }


    public void updateTime(LocalTime endTime, Integer betweenTime, Integer duration, Integer matchesSize, List<Match> matchList, int i) {
        LocalTime start = endTime.plusMinutes(betweenTime);
        LocalTime end = start.plusMinutes(duration);
        for (int j = i; j < matchesSize; j++) {
            matchList.get(j).setStartTime(start);
            matchList.get(j).setEndTime(end);
            matchService.updateMatch(matchList.get(j));
            start = end.plusMinutes(betweenTime);
            end = start.plusMinutes(duration);
        }
    }

}
