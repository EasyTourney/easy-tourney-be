package com.example.easytourneybe.match;

import com.example.easytourneybe.match.dto.ResultDto;
import com.example.easytourneybe.match.dto.RequestDragDropMatch;
import com.example.easytourneybe.model.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/tournament/{tournamentId}/match")
public class MatchController {
   @Autowired
    private MatchService matchService;
    @GetMapping("/result")
    public ResponseEntity<ResponseObject> getAllMatchResult(
            @PathVariable Integer tournamentId
    ) {
        List<ResultDto> result = matchService.getAllResult(tournamentId);
        ResponseObject responseObject = new ResponseObject(
                true, result.size(), result
        );
        return ResponseEntity.status(HttpStatus.OK).body(
                responseObject
        );
    }
    @PutMapping("/result/{matchID}")
    public ResponseEntity<ResponseObject> updateMatchResult(
            @PathVariable Integer tournamentId,
            @PathVariable Integer matchID,
            @RequestBody Match match
           ) {
        Match updateMatch=matchService.updateMatchResult(tournamentId,matchID, match.getTeamOneResult(), match.getTeamTwoResult());
        ResponseObject responseObject = new ResponseObject(
                true, 1, updateMatch
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }
    @PutMapping("{matchID}")
    public ResponseEntity<ResponseObject> updateMatchDetails(
            @PathVariable Integer tournamentId,
            @PathVariable Integer matchID,
            @RequestBody Match match
    ) {
        return matchService.updateMatchDetails(tournamentId,matchID, match.getTeamOneId(), match.getTeamTwoId(), match.getMatchDuration());
    }


    @PutMapping("/dragAndDrop")
    public ResponseEntity<?> dragAndDropMatchOrEvent(@RequestBody RequestDragDropMatch request)
    {
        var data = matchService.dragAndDropMatch(request.getMatchId(),
                request.getNewEventDateId(),
                request.getNewIndexOfMatch());
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .success(true)
                        .total(data.size())
                        .data(data)
                        .build()
        );
    }
}
