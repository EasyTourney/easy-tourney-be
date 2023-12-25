package com.example.easytourneybe.match;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class MatchService {
    @Autowired
    MatchRepository matchRepository;

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
            match.setId(Integer.parseInt(data[1].toString()));
            match.setTeamOneId(Integer.parseInt(data[2].toString()));
            match.setTeamTwoId(Integer.parseInt(data[3].toString()));
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
        List<Object[]> match= matchRepository.getAllResult(tournamentId);
        return processArrayData(match);
    }

    public Match updateMatch(Integer tournamentId,Integer matchID, Integer teamOneResult, Integer teamTwoResult) {
        checkMatchInTournament(tournamentId, matchID);
        Match match=matchRepository.findById(matchID).orElseThrow(()->new NoSuchElementException("Match not found")) ;
        if (teamOneResult==null || teamTwoResult==null) {
            throw new InvalidRequestException("Result must not be null");
        }
        if (teamOneResult<0 || teamTwoResult<0) {
            throw new InvalidRequestException("Result must be equal to 0 or greater than 0");
        }
        match.setTeamOneResult(teamOneResult);
        match.setTeamTwoResult(teamTwoResult);
        return matchRepository.save(match);
    }
}
