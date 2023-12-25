package com.example.easytourneybe.match;

import com.example.easytourneybe.model.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/tournament/{tournamentId}/match")
public class MatchController {
   @Autowired
    private  MatchService matchService;
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
    @PutMapping("/{matchID}")
    public ResponseEntity<ResponseObject> updateMatchResult(
            @PathVariable Integer tournamentId,
            @PathVariable Integer matchID,
            @RequestBody Match match
           ) {
        Match updateMatch=matchService.updateMatch(tournamentId,matchID, match.getTeamOneResult(), match.getTeamTwoResult());
        ResponseObject responseObject = new ResponseObject(
                true, 1, updateMatch
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }


}
