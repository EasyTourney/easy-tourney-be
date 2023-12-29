package com.example.easytourneybe.match;

import com.example.easytourneybe.enums.match.TypeMatch;
import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.match.dto.LeaderBoardDto;
import com.example.easytourneybe.match.dto.MatchDto;
import com.example.easytourneybe.match.dto.MatchOfLeaderBoardDto;
import com.example.easytourneybe.match.dto.ResultDto;
import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.dto.ResponseChangeMatch;
import com.example.easytourneybe.match.interfaces.IMatchRepository;
import com.example.easytourneybe.match.interfaces.IMatchService;
import com.example.easytourneybe.team.Team;
import com.example.easytourneybe.team.TeamService;
import com.example.easytourneybe.tournament.TournamentRepository;
import com.example.easytourneybe.util.MatchUtils;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService implements IMatchService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private IMatchRepository matchRepository;

    @Autowired
    private MatchUtils matchUtils;

    @Autowired
    private EventDateService eventDateService;

    @Override
    public List<List<Team>> matchList(Integer idTournament) {
        //If the tournament has matches, all old matches will be deleted to create a new generation
        if (matchRepository.isHaveMatchInTournament(idTournament)) {
            matchRepository.deleteMatchByTournamentId(idTournament);
        }

        //Check if the tournament has teams or not.
        List<Team> teams = teamService.getAllTeamByTournamentId(idTournament);
        if (teams.size() == 0) {
            throw new InvalidRequestException("Tournament currently has no teams.");
        }
        List<List<Team>> matches = new ArrayList<>();
        int numberTeams = teams.size();
        Team dummyTeam = new Team();

        //If the number of teams is odd, create a dummy team to make an even number of teams,
        //which will facilitate the team mapping process.
        if (numberTeams % 2 != 0) {
            teams.add(dummyTeam);
            numberTeams++;
        }
        for (int i = 0; i < numberTeams - 1; i++) {
            for (int j = 0; j < numberTeams / 2; j++) {
                List<Team> match = new ArrayList<>(2);

                //If neither of the two teams is a dummy team, we will be added to the match.
                if (ObjectUtils.allNotNull(teams.get(j).getTeamId(), teams.get(numberTeams - 1 - j).getTeamId())) {
                    match.add(teams.get(j));
                    match.add(teams.get(numberTeams - 1 - j));
                    matches.add(match);
                }
            }

            //Move the last team to index 1 to avoid repetition.
            Team lastTeam = teams.remove(numberTeams - 1);
            teams.add(1, lastTeam);
        }
        teams.remove(dummyTeam);
        return matches;
    }

    @Override
    public Map<EventDate, List<List<LocalTime>>> timeSheetMatches(Integer duration, Integer betweenTime, Integer numMatch, List<EventDate> eventDates) {
        if (numMatch < eventDates.size())
            throw new InvalidRequestException("The current number of matches is less than number of event dates");
        int numEvent = eventDates.size();

        //Calculate the average number of matches occurring per event date.
        int timeSheetEachEventDate = numMatch / numEvent;

        //Sort event dates by the number of available time slots they can be divided into.
        Map<EventDate, Integer> numberOfTimeSheet = matchUtils.timeSheet(duration, betweenTime, eventDates);
        eventDates.sort(Comparator.comparingInt(numberOfTimeSheet::get));
        Map<EventDate, List<List<LocalTime>>> schedule = new LinkedHashMap<>();

        //Allocate time slots available for each event date.
        for (int i = 0; i < eventDates.size(); i++) {
            LocalTime startMatch = eventDates.get(i).getStartTime();
            LocalTime endMatch = eventDates.get(i).getStartTime().plusMinutes(duration);
            LocalTime endDate = LocalTime.of(23, 59, 59);
            List<List<LocalTime>> times = new ArrayList<>();
            if (numMatch % numEvent >= numEvent) {
                timeSheetEachEventDate++;
            }
            int j = 0;
            LocalDateTime thisEventDate = LocalDateTime.of(eventDates.get(i).getDate(), endDate);
            LocalDateTime checkDateTime = LocalDateTime.of(eventDates.get(i).getDate(), startMatch);

            //Ensure that the time slots fall within the allowed time range for each event date.
            while (startMatch.isBefore(eventDates.get(i).getEndTime()) && endMatch.isBefore(eventDates.get(i).getEndTime())
                    && j < timeSheetEachEventDate && startMatch.isBefore(endDate) && endMatch.isBefore(endDate)) {
                List<LocalTime> matchTime = new ArrayList<>(2);
                matchTime.add(startMatch);
                matchTime.add(endMatch);
                times.add(matchTime);
                startMatch = endMatch.plusMinutes(betweenTime);
                endMatch = startMatch.plusMinutes(duration);
                checkDateTime = checkDateTime.plusMinutes(betweenTime);
                numMatch--;
                j++;
            }
            numEvent--;

            //If the number of time slots for that event date is less than the average number of matches occurring per event date,
            //then recalculate the average number of matches.
            if (times.size() < timeSheetEachEventDate && numEvent > 0) {
                timeSheetEachEventDate = numMatch / numEvent;
            }

            //If on the last event date the number of matches is still greater than the average number of matches per event date,
            //then add all matches to that last event date.
            if (i == eventDates.size() - 2 && numMatch > timeSheetEachEventDate) {
                timeSheetEachEventDate += numMatch - timeSheetEachEventDate;
            }

            schedule.put(eventDates.get(i), times);
        }

        //Return the result sorted by date.
        return schedule.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(EventDate::getDate)))
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
    }


    @Override
    public List<MatchDto> mappingMatchAndTime(List<List<Team>> matches, Map<EventDate, List<List<LocalTime>>> schedule) {
        List<Match> matchList = new ArrayList<>();
        List<MatchDto> matchDTOs;
        int j = 0;
        int duration;
        //Combine the matches and match times together.
        for (Map.Entry<EventDate, List<List<LocalTime>>> entry : schedule.entrySet()) {
            duration = tournamentRepository.findById(entry.getKey().getTournamentId()).get().getMatchDuration();
            for (int i = 0; i < entry.getValue().size(); i++, j++) {
                Match match = new Match();
                List<LocalTime> times = entry.getValue().get(i);
                match.setEventDateId(entry.getKey().getId());
                match.setStartTime(times.get(0));
                match.setEndTime(times.get(1));
                match.setTeamOneId(matches.get(j).get(0).getTeamId());
                match.setTeamTwoId(matches.get(j).get(1).getTeamId());
                match.setMatchDuration(duration);
                match.setType(TypeMatch.MATCH);
                matchList.add(match);
            }
        }
        matchRepository.saveAll(matchList);
        //Convert Match to MatchDto before returning.
        matchDTOs = schedule.keySet().stream()
                .flatMap(eventDate -> {
                    List<Match> lastMatches = matchRepository.getAllByEventDateId(eventDate.getId());
                    return lastMatches.stream();
                })
                .map(match -> matchUtils.convertMatchtoMatchDTO(match))
                .collect(Collectors.toList());

        return matchDTOs;
    }


    @Override
    public Match getMatchById(Long matchId) {
        return matchRepository.getById(matchId);
    }


    @Override
    public List<Match> getMatchByEventDateId(Integer eventDateId) {
        return matchRepository.getAllByEventDateId(eventDateId);
    }


    @Override
    public void saveAll(List<Match> matches) {
        matchRepository.saveAll(matches);
    }
    @Override
    @Transactional
    public List<GenerationDto> dragAndDropMatch(Integer matchId, Integer newEventDateId, Integer newIndexOfMatch) {
        Match match = matchRepository.findById(matchId).get();
        EventDate oldEventDate = eventDateService.findByEventDateId(match.getEventDateId()).get();
        EventDate newEventDate = eventDateService.findByEventDateId(newEventDateId).get();

        if (match.getStartTime().isBefore(LocalTime.now()) && oldEventDate.getDate().compareTo(LocalDate.now()) <= 0)
            throw new InvalidRequestException("Can not change match in the past");

        List<GenerationDto> response = null;
        if (oldEventDate.getDate().equals(newEventDate.getDate())) {
            response = dragAndDropMatchInDate(match, oldEventDate, newIndexOfMatch);
        } else {
            if (newEventDate.getDate().isBefore(LocalDate.now()))
                throw new InvalidRequestException("Can not change match to the past");
            response = dragAndDropMatchBetweenDate(match, oldEventDate, newEventDate, newIndexOfMatch);
        }
        return response;
    }


    private List<GenerationDto> dragAndDropMatchInDate(Match match, EventDate eventDate, Integer newIndexOfMatch) {

        List<Match> matches = matchRepository.findAllByEventDateId(match.getEventDateId());

        boolean isAddNewMatchInDate = false;
        boolean isRemoveMatchInDate = false;
        Integer newEventDateId = null;
        matches = changeTimeMatchInDate(match, matches,
                                        newIndexOfMatch, newEventDateId,
                                        isAddNewMatchInDate, isRemoveMatchInDate);

        matchRepository.saveAll(matches);

        //sort by start time and return
        matches = matches.stream().sorted(Comparator.comparing(Match::getStartTime)).collect(Collectors.toList());
        List<GenerationDto> result = new ArrayList<>();

        result.add(GenerationDto.builder()
                .eventDateId(eventDate.getId())
                .matches(matchUtils.convertMatchToMatchDto(matches))
                .date(eventDate.getDate())
                .startTime(eventDate.getStartTime())
                .endTime(eventDate.getEndTime())
                .build()
        );

        return result;
    }

    private List<GenerationDto> dragAndDropMatchBetweenDate(Match match, EventDate oldEventDate, EventDate newEventDate, Integer newIndexOfMatch) {
        /*
         Change all match in OldEventDate
        */
        List<Match> oldEventDateMatches = matchRepository.findAllByEventDateId(oldEventDate.getId());
        boolean isRemoveMatchInDate = true;
        boolean isAddNewMatchInDate = false;
        oldEventDateMatches = changeTimeMatchInDate(match, oldEventDateMatches,
                                                    newIndexOfMatch, newEventDate.getId(),
                                                    isAddNewMatchInDate, isRemoveMatchInDate);
        matchRepository.saveAll(oldEventDateMatches);

        /*
         change all match in newEventDate
        */
        List<Match> newEventDateMatches = matchRepository.findAllByEventDateId(newEventDate.getId());
        isRemoveMatchInDate = false;
        isAddNewMatchInDate = true;
        newEventDateMatches = changeTimeMatchInDate(match, newEventDateMatches,
                                                    newIndexOfMatch, newEventDate.getId(),
                                                    isAddNewMatchInDate, isRemoveMatchInDate);
        matchRepository.saveAll(newEventDateMatches);



        List<GenerationDto> result = new ArrayList<>();
        //sort by start time and return
        newEventDateMatches = newEventDateMatches.stream().sorted(Comparator.comparing(Match::getStartTime)).collect(Collectors.toList());
        oldEventDateMatches = oldEventDateMatches.stream().sorted(Comparator.comparing(Match::getStartTime)).collect(Collectors.toList());

        result.add(
                GenerationDto.builder()
                        .eventDateId(oldEventDate.getId())
                        .date(oldEventDate.getDate())
                        .startTime(oldEventDate.getStartTime())
                        .endTime(oldEventDate.getEndTime())
                        .matches(matchUtils.convertMatchToMatchDto(oldEventDateMatches))
                        .build()
        );

        result.add(
                GenerationDto.builder()
                        .eventDateId(newEventDate.getId())
                        .date(newEventDate.getDate())
                        .startTime(newEventDate.getStartTime())
                        .endTime(newEventDate.getEndTime())
                        .matches(matchUtils.convertMatchToMatchDto(newEventDateMatches))
                        .build()
        );
        return result;
    }

    private List<Match> changeTimeMatchInDate(Match match, List<Match> matchesInDate,
                                              Integer newIndexOfMatch, Integer newEventDateId,
                                              boolean isAddNewMatchInDate, boolean isRemoveMatchInDate) {
        // sort match by time
        matchesInDate = matchesInDate.stream().sorted(Comparator.comparing(Match::getStartTime)).collect(Collectors.toList());

        Integer oldIndex = null;
        if (!isAddNewMatchInDate)
            oldIndex = matchesInDate.indexOf(matchesInDate.stream().filter(eachMatch -> eachMatch.getId().equals(match.getId())).findFirst().get());

        int duration = match.getMatchDuration();
        int betweenTime = 10;
        int timeChange = duration + betweenTime;
        LocalTime newStartTime = null;
        LocalTime newEndTime = null;
        int timeDifference = 0;
        if (!isAddNewMatchInDate && !isRemoveMatchInDate) {
            if (oldIndex < newIndexOfMatch) {
                for (int index = oldIndex + 1; index < newIndexOfMatch; index++) {
                    Match eachMatch = matchesInDate.get(index);
                    newStartTime = eachMatch.getStartTime();
                    newEndTime = eachMatch.getEndTime();
                    timeDifference = duration - eachMatch.getMatchDuration();
                    eachMatch.setEndTime(newEndTime.minusMinutes(timeChange));
                    eachMatch.setStartTime(newStartTime.minusMinutes(timeChange));
                    matchesInDate.set(index, eachMatch);
                }
                match.setStartTime(newStartTime.minusMinutes(timeDifference));
                match.setEndTime(match.getStartTime().plusMinutes(duration));
                matchesInDate.set(oldIndex, match);
            } else {
                for (int index = oldIndex - 1; index >= newIndexOfMatch - 1; index--) {
                    Match eachMatch = matchesInDate.get(index);
                    newStartTime = eachMatch.getStartTime();
                    newEndTime = eachMatch.getEndTime();
                    eachMatch.setEndTime(newEndTime.plusMinutes(timeChange));
                    eachMatch.setStartTime(newStartTime.plusMinutes(timeChange));
                    matchesInDate.set(index, eachMatch);
                }
                match.setStartTime(newStartTime.minusMinutes(timeDifference));
                match.setEndTime(match.getStartTime().plusMinutes(duration));
                matchesInDate.set(oldIndex, match);
            }
        }

        if (isRemoveMatchInDate) {
            for (int index = oldIndex + 1; index < matchesInDate.size(); index++) {
                Match eachMatch = matchesInDate.get(index);
                newStartTime = eachMatch.getStartTime();
                newEndTime = eachMatch.getEndTime();
                eachMatch.setEndTime(newEndTime.minusMinutes(timeChange));
                eachMatch.setStartTime(newStartTime.minusMinutes(timeChange));
                matchesInDate.set(index, eachMatch);
            }
            matchesInDate.remove(oldIndex);
        }

        if (isAddNewMatchInDate) {
            boolean isAddToTheEnd = true;
            for (int index = matchesInDate.size() - 1; index >= newIndexOfMatch - 1; index--) {
                Match eachMatch = matchesInDate.get(index);
                newStartTime = eachMatch.getStartTime();
                newEndTime = eachMatch.getEndTime();
                eachMatch.setEndTime(newEndTime.plusMinutes(timeChange));
                eachMatch.setStartTime(newStartTime.plusMinutes(timeChange));
                if (eachMatch.getEndTime().isBefore(newEndTime))
                    throw new InvalidRequestException("Not enough time to schedule");
                matchesInDate.set(index, eachMatch);
                isAddToTheEnd = false;
            }
            if (isAddToTheEnd) {
                if (matchesInDate.isEmpty())
                    newStartTime = eventDateService.findByEventDateId(newEventDateId).get().getStartTime();
                else
                    newStartTime = matchesInDate.get(matchesInDate.size() - 1).getEndTime().plusMinutes(betweenTime);
            }

            match.setStartTime(newStartTime.minusMinutes(timeDifference));
            match.setEndTime(match.getStartTime().plusMinutes(duration));
            match.setEventDateId(newEventDateId);
            matchesInDate.add(match);
        }

        return matchesInDate;
    }

    public boolean isHaveMatchInDate(Integer eventDateId) {
        return matchRepository.isHaveMatchInDate(eventDateId);
    }

    private void checkMatchInTournament(Integer tournamentId, Integer matchID) {
        if (!matchRepository.isMatchInTournament(tournamentId, matchID)) {
            throw new NoSuchElementException("This match is not in this tournament");
        }
    }

    public List<ResultDto> processArrayData(List<Object[]> arrayData) {
        Map<LocalDate, ResultDto> resultMap = new HashMap<>();

        for (Object[] data : arrayData) {
            LocalDate date = LocalDate.parse(data[0].toString());
            ResultDto resultDto = resultMap.get(date);

            if (resultDto == null) {
                resultDto = new ResultDto();
                resultDto.setDate(date);
                resultDto.setMatches(new ArrayList<>());
                resultMap.put(date, resultDto);
            }

            Match match = new Match();
            match.setId(Long.parseLong(data[1].toString()));
            match.setTeamOneId(Long.parseLong(data[2].toString()));
            match.setTeamTwoId(Long.parseLong(data[3].toString()));
            match.setTeamOneResult(Integer.parseInt(data[4].toString()));
            match.setTeamTwoResult(Integer.parseInt(data[5].toString()));
            match.setStartTime(LocalTime.parse(data[6].toString()));
            match.setEndTime(LocalTime.parse(data[7].toString()));
            match.setEventDateId(Integer.parseInt(data[8].toString()));

            resultDto.getMatches().add(match);
        }

        return new ArrayList<>(resultMap.values());
    }

    public List<ResultDto> getAllResult(Integer tournamentId) {
        List<Object[]> match = matchRepository.getAllResult(tournamentId);
        return processArrayData(match);
    }

    public Match updateMatch(Integer tournamentId, Integer matchID, Integer teamOneResult, Integer teamTwoResult) {
        checkMatchInTournament(tournamentId, matchID);
        Match match = matchRepository.findById(matchID).orElseThrow(() -> new NoSuchElementException("Match not found"));
        if (teamOneResult == null || teamTwoResult == null) {
            throw new InvalidRequestException("Result must not be null");
        }
        if (teamOneResult < 0 || teamTwoResult < 0) {
            throw new InvalidRequestException("Result must be equal to 0 or greater than 0");
        }
        match.setTeamOneResult(teamOneResult);
        match.setTeamTwoResult(teamTwoResult);
        return matchRepository.save(match);
    }

    @Override
    public List<LeaderBoardDto> getLeaderBoardByTournamentId(Integer tournamentId) {
        return matchRepository.getLeaderBoard(tournamentId);
    }

    @Override
    public List<MatchOfLeaderBoardDto> getMatchOfLeaderBoardByTournamentId(Integer tournamentId) {
        return matchRepository.getMatchOfLeaderBoard(tournamentId);
    }
}
