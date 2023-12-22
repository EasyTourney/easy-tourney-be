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

    public List<EventDate> saveAll(List<EventDate> eventDates) {
        return eventDateRepository.saveAll(eventDates);
    }

    public void deleteAllByTournamentId(Integer tournamentId) {
        eventDateRepository.deleteAllByTournamentId(tournamentId);
    }

    public void deleteByEventDateId(Integer eventDateId) {
        eventDateRepository.deleteByEventDateId(eventDateId);
    }

    public void createEventDate(EventDate eventDate) {
        eventDateRepository.save(eventDate);
    }
}
