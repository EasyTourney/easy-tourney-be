package com.example.easytourneybe.tournament;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    TournamentService tournamentService;

    @GetMapping()
    public ResponseEntity<?> getAll(@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                    @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String field,
                                    @RequestParam(name = "sortType", required = false, defaultValue = "DESC") String sortType,
                                    @RequestParam(name = "filterStatus", required = false) TournamentStatus status,
                                    @RequestParam(name = "search", required = false, defaultValue = "") String search
                                    ) {
            return ResponseEntity.ok(tournamentService.getAll(page-1, pageSize, field, sortType.toUpperCase(), status, search));
    }
}
