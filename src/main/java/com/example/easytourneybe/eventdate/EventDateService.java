package com.example.easytourneybe.eventdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventDateService {

    @Autowired
    EventDateRepository eventDateRepository;
    public List<EventDate> findAllByTournamentId(Integer tournamentId) {
        return eventDateRepository.findAllByTournamentId(tournamentId);
    };
}
