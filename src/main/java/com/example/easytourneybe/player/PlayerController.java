package com.example.easytourneybe.player;

import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.player.dto.Player;
import com.example.easytourneybe.player.dto.PlayerRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/tournament/{tournamentId}/team/{teamID}/player")
public class PlayerController {
    @Autowired
    private PlayerService playerService;
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllPlayersByTeamID(
            @PathVariable Long teamID
    ) {
        Long totalPlayers = playerService.getTotalPlayers(teamID);
        List<Player> player = playerService.getAllPlayersByTeamID(teamID);
        ResponseObject responseObject = new ResponseObject(
                true, player.size(), player
        );
        responseObject.setAdditionalData(java.util.Collections.singletonMap("totalPlayer", totalPlayers));
        return ResponseEntity.status(HttpStatus.OK).body(
                responseObject
        );
    }
    @PostMapping("")
    public ResponseEntity<ResponseObject> createPlayer(
            @PathVariable Long teamID,
           @Valid @RequestBody PlayerRequestDto player
    ) {
        Player newPlayer=playerService.createPlayer(teamID, player.getPlayerName(), player.getDateOfBirth(), player.getPhone());
        ResponseObject responseObject = new ResponseObject(
                true, 1, newPlayer
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
    }

    @PutMapping("/{playerID}")
    public ResponseEntity<ResponseObject> updatePlayer(
            @PathVariable Long teamID,
            @PathVariable Long playerID,
            @Valid @RequestBody PlayerRequestDto player
    ) {
        Player updatePlayer=playerService.updatePlayer(teamID,playerID, player.getPlayerName().trim(), player.getDateOfBirth(), player.getPhone().trim());
        ResponseObject responseObject = new ResponseObject(
                true, 1, updatePlayer
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    @DeleteMapping("/{playerID}")
    public ResponseEntity<ResponseObject> deletePlayer(
            @PathVariable Long teamID,
            @PathVariable Long playerID
    ) {
        Player deletedPlayer= playerService.deletePlayer(teamID,playerID);
        ResponseObject responseObject = new ResponseObject(
                true, 1, deletedPlayer
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }
}
