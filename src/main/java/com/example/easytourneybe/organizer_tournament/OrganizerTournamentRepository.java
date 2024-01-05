package com.example.easytourneybe.organizer_tournament;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganizerTournamentRepository extends JpaRepository<OrganizerTournament, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM OrganizerTournament ot WHERE ot.tournamentId = :tournamentId")
    void deleteAllByTournamentId(Integer tournamentId);

    List<OrganizerTournament> findAllByTournamentId(Integer tournamentId);
}
