package com.example.easytourneybe.team;

import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.team.dto.TeamPlayerDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import static com.example.easytourneybe.constants.DefaultListParams.PAGE;
import static com.example.easytourneybe.constants.DefaultListParams.SIZE;


@RestController
@CrossOrigin
@RequestMapping("/tournament/{tournament_id}")
public class TeamController {
    @Autowired
    private TeamService teamService;
    @GetMapping("/team")
    public ResponseEntity<ResponseObject> findAllTeamAndPlayer(
            @PathVariable Integer tournament_id ,
            @RequestParam(defaultValue = SIZE) Integer size,
            @RequestParam(defaultValue = PAGE) Integer page
    ) {
        Long totalTeamRecords = teamService.getTotalRecordsForTournament(tournament_id);
        List<TeamPlayerDto> teamAndPlayer = teamService.getAllTeamAndPlayerCount(tournament_id, page-1,size);
        ResponseObject responseObject = new ResponseObject(
                true, teamAndPlayer.size(), teamAndPlayer
        );
        responseObject.setAdditionalData(java.util.Collections.singletonMap("totalTeamOfTournament", totalTeamRecords));
        return ResponseEntity.status(HttpStatus.OK).body(
                responseObject
        );
    }
    @PostMapping("/team")
    public ResponseEntity<ResponseObject> createTeam(@Valid @RequestBody Team team,
                                                     @PathVariable Integer tournament_id) {

        Team temp = teamService.createTeam(team.getTeamName().trim(), tournament_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject(true, 1, temp)
        );
    }
    @PutMapping("/team/{id}")
    public ResponseEntity<ResponseObject> updateTeam(@PathVariable Integer tournament_id, @PathVariable Integer id, @Valid @RequestBody Team team) {
        Optional<Team> updateTeam = teamService.updateTeam(tournament_id, id, team.getTeamName().trim());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, 1, updateTeam)
        );
    }
    @DeleteMapping("/team/{id}")
    public ResponseEntity<ResponseObject> deleteTeam(@PathVariable Integer tournament_id, @PathVariable Integer id) {
        Optional<Team> deleteTeam = teamService.deleteTeam(tournament_id, id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, 1, deleteTeam));
    }
}
