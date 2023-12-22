package com.example.easytourneybe.organizer_tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerTournamentService {
    @Autowired
    private OrganizerTournamentRepository organizerTournamentRepository;

    public OrganizerTournament save(OrganizerTournament organizerTournament) {
        return organizerTournamentRepository.save(organizerTournament);
    }

    public void deleteAllByTournamentId(Integer tournamentId) {
        organizerTournamentRepository.deleteAllByTournamentId(tournamentId);
    }

    public void saveAll(List<OrganizerTournament> organizerTournamentList) {

        organizerTournamentRepository.saveAll(organizerTournamentList);
    }
}
