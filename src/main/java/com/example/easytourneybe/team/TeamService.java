package com.example.easytourneybe.team;

import com.example.easytourneybe.team.dto.TeamPlayerDto;
import com.example.easytourneybe.team.interfaces.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;


       public List<TeamPlayerDto> getAllTeamAndPlayerCount(Long id, Integer page, Integer size) {
            Pageable pageable= PageRequest.of(page,size);
           return teamRepository.getAllTeamAndPlayerCount(id, pageable).stream()
                   .map(teamData -> new TeamPlayerDto((Long) teamData[0], (String) teamData[1], (Long) teamData[2]))
                   .collect(Collectors.toList());

   }
        public long getTotalRecordsForTournament(Long id) {
            return teamRepository.getTotalRecordsForTournament(id);
    }
}