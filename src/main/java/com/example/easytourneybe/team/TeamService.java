package com.example.easytourneybe.team;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.match.interfaces.IMatchRepository;
import com.example.easytourneybe.team.dto.TeamPlayerDto;
import com.example.easytourneybe.team.interfaces.TeamRepository;
import com.example.easytourneybe.tournament.Tournament;
import com.example.easytourneybe.tournament.TournamentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.easytourneybe.enums.tournament.TournamentStatus.NEED_INFORMATION;
import static com.example.easytourneybe.util.TournamentStatusPermission.allowGenerateStatus;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    IMatchRepository matchRepository;

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
    public Optional<Team> updateTeam(Integer tournamentId, Long id, String teamName) {
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
    public Optional<Team> deleteTeam(Integer tournamentId, Long id) {
        Tournament tournament = tournamentRepository.findTournamentByIdAndIsDeletedFalse(tournamentId).orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        if (!allowGenerateStatus.contains(tournament.getStatus())) {
            throw new InvalidRequestException("You cannot delete a team from a tournament that is in progress, finished, or discarded.");
        }

        matchRepository.deleteMatchByTournamentId(tournamentId);
        tournament.setStatus(NEED_INFORMATION);
        tournamentRepository.save(tournament);

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

    public Team findTeamById(Integer tournamentId, Long id) {
           Team team = teamRepository.findTeamById(tournamentId, id).orElseThrow(() -> new NoSuchElementException("Team not found"));
          return team;
    }

    public List<Team> getAllTeamByTournamentId(Integer idTournament){return teamRepository.findTeamByTournamentId(idTournament);}
    public Team getTeamById(Long id) {
        return teamRepository.getTeamByTeamId(id);
    }

    public boolean checkTeamExist(Integer tournamentId, Long teamId) {
        return teamRepository.findTeamById(tournamentId, teamId).isPresent();
    }
}