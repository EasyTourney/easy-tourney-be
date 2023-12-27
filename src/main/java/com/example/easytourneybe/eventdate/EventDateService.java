package com.example.easytourneybe.eventdate;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import static com.example.easytourneybe.util.TimeValidateUtils.ParseStringToTime;

@Service
public class EventDateService {

    @Autowired
    EventDateRepository eventDateRepository;
    public List<EventDate> findAllByTournamentId(Integer tournamentId) {
        return eventDateRepository.findAllByTournamentId(tournamentId);
    };

    public void saveAll(List<EventDate> eventDates) {
         eventDateRepository.saveAll(eventDates);
    }

    public void deleteAllByTournamentId(Integer tournamentId) {
        eventDateRepository.deleteAllByTournamentId(tournamentId);
    }

    public void deleteByEventDateId(Integer eventDateId) {
        eventDateRepository.deleteByEventDateId(eventDateId);
    }


    public Optional<EventDate> findByEventDateId(Integer eventDateId) {
        return eventDateRepository.findById(eventDateId);
    }

    public EventDate updateStarTimeAndEndTime(Integer tournamentId, Integer eventDateId, String startTime, String endTime) {
        EventDate eventDate = eventDateRepository.findById(eventDateId).orElseThrow(
                () -> new NoSuchElementException("EventDate with id " + eventDateId + " does not exist"));

        LocalTime startTimeValid = ParseStringToTime(startTime,"Start time must be valid");
        LocalTime endTimeValid = ParseStringToTime(endTime,"End time must be valid");

        if (!Objects.equals(tournamentId, eventDate.getTournamentId())) {
            throw new NoSuchElementException("EventDate with id " + eventDateId + " does not in this tournament");}

        if (startTimeValid.isAfter(endTimeValid)) {
            throw new InvalidRequestException("Start time must be before end time");
        }

        if(startTimeValid.equals(endTimeValid)){
            throw new InvalidRequestException("Start time and end time are the same");
        }

        if(eventDate.getDate().equals(LocalDate.now()) && startTimeValid.isBefore(LocalTime.now())){
            throw new InvalidRequestException("Start time must be after current time");
        }

        eventDate.setStartTime(startTimeValid);
        eventDate.setEndTime(endTimeValid);
        eventDate.setUpdatedAt(LocalDateTime.now());
        return eventDateRepository.save(eventDate);
    }
}
