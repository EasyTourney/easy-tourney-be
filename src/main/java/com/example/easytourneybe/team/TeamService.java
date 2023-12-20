package com.example.easytourneybe.team;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.team.dto.TeamPlayerDto;
import com.example.easytourneybe.team.interfaces.TeamRepository;
import jakarta.transaction.Transactional;
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
    public boolean hasExistTeamName(Integer tournamentId, String teamName) {
        return !teamRepository.findTeamsByName(tournamentId, teamName.trim()).isEmpty();
    }
    public Team createTeam (String teamName, Integer tournamentId) {

        if (hasExistTeamName(tournamentId, teamName.trim())) {
            throw new InvalidRequestException("Team name has already existed");
        }
        Team team = new Team();
        team.setTournamentId(tournamentId);
        team.setTeamName(teamName.trim());
        team.setCreatedAt(LocalDateTime.now());
        return teamRepository.save(team);
    }
    public Optional<Team> updateTeam(Integer tournamentId, Integer id, String teamName) {
        Optional<Team> teamOptional = teamRepository.findTeamById(tournamentId, id);

        if(hasExistTeamName(tournamentId, teamName.trim())){
            throw new InvalidRequestException("Team name has already exist");
        }

        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            team.setTeamName(teamName);
            team.setUpdatedAt(LocalDateTime.now());
            team.setTournamentId(tournamentId);
            teamRepository.save(team);
            return Optional.of(team);
        } else {
            throw new NoSuchElementException("Team not found");
        }
    }
    @Transactional
    public Optional<Team> deleteTeam(Integer tournamentId, Integer id) {
        Optional<Team> teamOptional = teamRepository.findTeamById(tournamentId,id);

        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            teamRepository.deleteByTournamentIdAndTeamId(tournamentId, id);
            return Optional.of(team);
        } else {
            throw new NoSuchElementException("Category not found");
        }
    }
        public long getTotalRecordsForTournament(Integer id) {
            return teamRepository.getTotalRecordsForTournament(id);
    }
}