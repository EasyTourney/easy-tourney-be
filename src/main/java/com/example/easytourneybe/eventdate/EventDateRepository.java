package com.example.easytourneybe.eventdate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDateRepository extends JpaRepository<EventDate, Integer> {
    List<EventDate> findAllByTournamentId(Integer tournamentId);
}
