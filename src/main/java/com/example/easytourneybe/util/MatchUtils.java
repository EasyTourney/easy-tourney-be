package com.example.easytourneybe.util;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.enums.match.TypeMatch;
import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.Match;
import com.example.easytourneybe.match.dto.MatchDto;

import com.example.easytourneybe.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import java.util.stream.Collectors;

@Component
public class MatchUtils {
    @Autowired
    private TeamService teamService;


    public Integer numberMatchTimes(Map<EventDate, List<List<LocalTime>>> schedule) {
        int numMatchTimes = 0;
        for (Map.Entry<EventDate, List<List<LocalTime>>> entry : schedule.entrySet()) {
            numMatchTimes += entry.getValue().size();
        }
        return numMatchTimes;
    }

    public boolean compareNumMatchAndNumMatchTime(int numMatch, int numMatchTime) {
        return numMatchTime >= numMatch;
    }


    public MatchDto convertMatchtoMatchDTO(Match match) {
        MatchDto matchDTO = new MatchDto();
        matchDTO.setId(match.getId());
        if (match.getType().equals(TypeMatch.MATCH)) {
            matchDTO.setTeamOne(teamService.getTeamById(match.getTeamOneId()));
            matchDTO.setTeamTwo(teamService.getTeamById(match.getTeamTwoId()));
        }
        matchDTO.setStartTime(match.getStartTime());
        matchDTO.setEndTime(match.getEndTime());
        matchDTO.setEventDateId(match.getEventDateId());
        matchDTO.setType(match.getType());
        matchDTO.setTitle(match.getTitle());
        return matchDTO;
    }

    public GenerationDto createGeneration(Optional<EventDate> eventDate, List<MatchDto> matchDTOs) {
        GenerationDto generationDTO = new GenerationDto();
        if (eventDate.isPresent()) {
            generationDTO.setEventDateId(eventDate.get().getId());
            generationDTO.setDate(eventDate.get().getDate());
            generationDTO.setStartTime(eventDate.get().getStartTime());
            generationDTO.setEndTime(eventDate.get().getEndTime());
            List<MatchDto> matchDTOByEventDateId = new ArrayList<>();
            for (MatchDto matchDTO : matchDTOs) {
                if (Objects.equals(matchDTO.getEventDateId(), eventDate.get().getId())) {
                    matchDTOByEventDateId.add(matchDTO);
                }
            }
            generationDTO.setMatches(matchDTOByEventDateId);
        }
        return generationDTO;
    }

    public Map<EventDate, Integer> timeSheet(Integer duration, Integer betweenTime, List<EventDate> eventDates) {
        Map<EventDate, Integer> numberOfTimeEachEvent = new LinkedHashMap<>();
        LocalTime endDate = LocalTime.of(23, 59, 59);
        for (EventDate eventDate : eventDates) {
            LocalTime startMatch = eventDate.getStartTime();
            LocalTime endMatch = eventDate.getStartTime().plusMinutes(duration);
            LocalDateTime thisEventDate = LocalDateTime.of(eventDate.getDate(), endDate);
            LocalDateTime checkDateTime = LocalDateTime.of(eventDate.getDate(), startMatch);
            Integer countTime = 0;
            while (startMatch.isBefore(eventDate.getEndTime()) && endMatch.isBefore(eventDate.getEndTime()) && checkDateTime.isBefore(thisEventDate)) {
                countTime++;
                startMatch = endMatch.plusMinutes(betweenTime);
                endMatch = startMatch.plusMinutes(duration);
                checkDateTime = checkDateTime.plusMinutes(betweenTime + duration);
            }
            numberOfTimeEachEvent.put(eventDate, countTime);
        }

        return numberOfTimeEachEvent.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(EventDate::getDate)))
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
    }

    public List<MatchDto> convertMatchListToMatchDtoList(List<Match> matches) {
        return matches.stream()
                .map(this::convertMatchtoMatchDTO)
                .toList();
    }

    public List<MatchDto> convertMatchToMatchDto(List<Match> matches) {
        return matches.stream().map(this::convertMatchtoMatchDTO).sorted(Comparator.comparing(MatchDto::getStartTime)).collect(Collectors.toList());
    }


}
