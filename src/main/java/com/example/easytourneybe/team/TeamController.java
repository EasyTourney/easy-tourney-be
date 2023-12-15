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
@RequestMapping("/team")
public class TeamController {
    @Autowired
    private TeamService TeamService;
    @GetMapping("/all")
    public ResponseEntity<ResponseObject> findAllTeamAndPlayer(
            @RequestParam(defaultValue = SIZE) Integer size,
            @RequestParam(defaultValue = PAGE) Integer page


    ) {
        List<TeamPlayerDto> teamAndPlayer = TeamService.getAllTeamAndPlayerCount(page-1,size);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, teamAndPlayer.size(), teamAndPlayer)
        );
    }
}
