package com.example.easytourneybe.team;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.team.dto.TeamPlayerDto;
import com.example.easytourneybe.team.interfaces.TeamRepository;
import jakarta.transaction.Transactional;
import com.example.easytourneybe.tournament.TournamentRepository;
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
    @Autowired
    private TournamentRepository tournamentRepository;
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
        if(tournamentRepository.findTournamentById(tournamentId).isEmpty()) {
            throw new InvalidRequestException("Tournament has been deleted or discarded or finished");
        }
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
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            if (!team.getTeamName().equals(teamName.trim())) {
                if (hasExistTeamName(tournamentId, teamName.trim())) {
                    throw new InvalidRequestException("Team name has already exist");
                }
            }
            team.setTeamName(teamName.trim());
            team.setUpdatedAt(LocalDateTime.now());
            team.setTournamentId(tournamentId);
            teamRepository.save(team);

            return Optional.of(team);
        } else {
            throw new NoSuchElementException("Not found team or team belonging to a tournament that has been deleted or discarded, please check again");
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
            throw new NoSuchElementException("Not found team or team belonging to a tournament that has been deleted or discarded, please check again");
        }
    }
        public long getTotalRecordsForTournament(Integer id) {
            return teamRepository.getTotalRecordsForTournament(id);
    }
    public void deleteTeamByTournamentId(Integer tournamentId) {

        List<Team> foundTeam = teamRepository.findTeamByTournamentId(tournamentId);
        teamRepository.deleteAll(foundTeam);
    }

    public Team findTeamById(Integer tournamentId, Integer id) {
           Team team = teamRepository.findTeamById(tournamentId, id).orElseThrow(() -> new NoSuchElementException("Team not found"));
          return team;
    }

    public List<Team> getAllTeamByTournamentId(Integer idTournament){return teamRepository.findTeamByTournamentId(idTournament);}
    public Team getTeamById(Long id) {
        return teamRepository.getTeamByTeamId(id);
    }
}