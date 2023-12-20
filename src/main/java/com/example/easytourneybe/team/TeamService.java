package com.example.easytourneybe.team;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.team.dto.TeamPlayerDto;
import com.example.easytourneybe.team.interfaces.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

       public List<TeamPlayerDto> getAllTeamAndPlayerCount(Integer id, Integer page, Integer size) {
            Pageable pageable= PageRequest.of(page,size);
           return teamRepository.getAllTeamAndPlayerCount(id, pageable).stream()
                   .map(teamData -> new TeamPlayerDto((Long) teamData[0], (String) teamData[1], (Long) teamData[2]))
                   .collect(Collectors.toList());

   }
    public boolean hasExistTeamName(String teamName) {
        return !teamRepository.findTeamsByName(teamName.trim()).isEmpty();
    }
    public Team createTeam (String teamName, Integer id) {

        if (hasExistTeamName(teamName.trim())) {
            throw new InvalidRequestException("Team name has already existed");
        }
        Team team = new Team();
        team.setTournamentId(id);
        team.setTeamName(teamName.trim());
        team.setCreatedAt(LocalDateTime.now());
        return teamRepository.save(team);
    }
        public long getTotalRecordsForTournament(Integer id) {
            return teamRepository.getTotalRecordsForTournament(id);
    }
}