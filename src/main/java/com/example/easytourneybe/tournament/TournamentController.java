package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.model.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    TournamentService tournamentService;

    @GetMapping()
    public ResponseEntity<?> getAll(@RequestParam(name = "size", required = false, defaultValue = "10") Integer pageSize,
                                    @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "sortValue", required = false, defaultValue = "createdAt") String field,
                                    @RequestParam(name = "sortType", required = false, defaultValue = "DESC") String sortType,
                                    @RequestParam(name = "filterStatus", required = false) TournamentStatus status,
                                    @RequestParam(name = "keyword", required = false, defaultValue = "") String search,
                                    @RequestParam(name = "categoryId", required = false) Integer categoryId
                                    ) {

        return ResponseEntity.ok(tournamentService.getAll(page-1, pageSize, field, sortType.toUpperCase(), status, search.trim(), categoryId));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Integer id) {
        Optional<Tournament> deleteTournament = tournamentService.deleteTournament(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, 1, deleteTournament));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getTournamentGeneralInfo(@PathVariable Integer id) {
        ResponseObject tournamentResponse = tournamentService.getTournamentToShowGeneral(id);
        return ResponseEntity.status(HttpStatus.OK).body(tournamentResponse);
    }
}
