package com.example.easytourneybe.team;

import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.team.dto.TeamPlayerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.easytourneybe.constants.DefaultListParams.PAGE;
import static com.example.easytourneybe.constants.DefaultListParams.SIZE;


@RestController
@CrossOrigin
@RequestMapping("/tournament")
public class TeamController {
    @Autowired
    private TeamService TeamService;
    @GetMapping("/{id}/team")
    public ResponseEntity<ResponseObject> findAllTeamAndPlayer(
            @PathVariable Long id ,
            @RequestParam(defaultValue = SIZE) Integer size,
            @RequestParam(defaultValue = PAGE) Integer page
    ) {
        Long totalTeamRecords = TeamService.getTotalRecordsForTournament(id);
        List<TeamPlayerDto> teamAndPlayer = TeamService.getAllTeamAndPlayerCount(id, page-1,size);
        ResponseObject responseObject = new ResponseObject(
                true, teamAndPlayer.size(), teamAndPlayer
        );
        responseObject.setAdditionalData(java.util.Collections.singletonMap("totalTeamOfTournament", totalTeamRecords));
        return ResponseEntity.status(HttpStatus.OK).body(
                responseObject
        );
    }
}
