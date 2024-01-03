package com.example.easytourneybe.match.interfaces;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.Match;
import com.example.easytourneybe.match.dto.LeaderBoardDto;
import com.example.easytourneybe.match.dto.MatchDto;
import com.example.easytourneybe.match.dto.MatchOfLeaderBoardDto;
import com.example.easytourneybe.match.dto.ResponseChangeMatch;
import com.example.easytourneybe.team.Team;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface IMatchService {
    List<List<Team>> matchList(Integer idTournament);

    Map<EventDate, List<List<LocalTime>>> timeSheetMatches(Integer duration, Integer betweenTime, Integer numMatch, List<EventDate> eventDates);

    List<MatchDto> mappingMatchAndTime(List<List<Team>> matches, Map<EventDate, List<List<LocalTime>>> schedule,Integer duration);

    Match getMatchById(Long matchId);


    List<Match> getMatchByEventDateId(Integer eventDateId);


    List<LeaderBoardDto> getLeaderBoardByTournamentId(Integer tournamentId);

    List<MatchOfLeaderBoardDto> getMatchOfLeaderBoardByTournamentId(Integer tournamentId);

    void saveAll(List<Match> matches);


    List<GenerationDto> dragAndDropMatch(Integer matchId, Integer newEventDateId, Integer newIndexOfMatch);
}
