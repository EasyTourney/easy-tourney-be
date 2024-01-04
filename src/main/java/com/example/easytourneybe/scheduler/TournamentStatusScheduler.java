package com.example.easytourneybe.scheduler;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.tournament.Tournament;
import com.example.easytourneybe.tournament.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentStatusScheduler {

    /*
    NEED_INFORMATION,
    READY,
    IN_PROGRESS,
    FINISHED,
    DISCARDED,
    DELETED
     */

    @Autowired
    TournamentService tournamentService;

    @Scheduled(fixedRate = 60000)
    public void updateTournamentStatus() {
        List<Tournament> tournaments = new ArrayList<>();
        // update need info to finished if that have no match and get in start time in the first event date
        tournaments = tournamentService.findTournamentNeedInformationNeedToChangeToFinished();
        tournaments = tournaments.stream().map(tournament -> {
            tournament.setStatus(TournamentStatus.FINISHED);
            return tournament;
        }).toList();
        tournamentService.saveAll(tournaments);

        // update ready to in_progress
        tournaments = new ArrayList<>();
        tournaments = tournamentService.findTournamentReadyNeedToChangeToInProgress();
        tournaments = tournaments.stream().map(tournament -> {
            tournament.setStatus(TournamentStatus.IN_PROGRESS);
            return tournament;
        }).toList();
        tournamentService.saveAll(tournaments);

        // update in_progress to finished
        tournaments = new ArrayList<>();
        tournaments = tournamentService.findTournamentInProgressNeedToChangeToFinished();
        tournaments = tournaments.stream().map(tournament -> {
            tournament.setStatus(TournamentStatus.FINISHED);
            return tournament;
        }).toList();
        tournamentService.saveAll(tournaments);
    }

}
