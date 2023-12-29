package com.example.easytourneybe.eventdate;

import com.example.easytourneybe.eventdate.dto.EventDate;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface EventDateRepository extends JpaRepository<EventDate, Integer> {
    List<EventDate> findAllByTournamentId(Integer tournamentId);

    @Query(value = "SELECT e.date FROM event_date e WHERE e.tournament_id = :tournamentId", nativeQuery = true)
    List<LocalDate> findAllDateByTournamentId(Integer tournamentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM event_date e WHERE e.tournament_id = :tournamentId", nativeQuery = true)
    void deleteAllByTournamentId(Integer tournamentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM event_date e WHERE e.id = :eventDateId", nativeQuery = true)
    void deleteByEventDateId(Integer eventDateId);

    Optional<EventDate> findById(Integer eventDateId);
}
