package com.example.easytourneybe.match.interfaces;

import com.example.easytourneybe.eventdate.EventDate;
import com.example.easytourneybe.match.Match;
import com.example.easytourneybe.match.MatchDto;
import com.example.easytourneybe.team.Team;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface IMatchService {
    List<List<Team>> matchList(Integer idTournament);

    Map<EventDate, List<List<LocalTime>>> timeSheetMatches(Integer duration, Integer betweenTime, Integer numMatch, List<EventDate> eventDates);

    List<MatchDto> mappingMatchAndTime(List<List<Team>> matches, Map<EventDate, List<List<LocalTime>>> schedule);

    Match getMatchById(Long matchId);

    void updateMatch(Match match);

    List<Match> getMatchByEventDateId(Integer eventDateId);
}
