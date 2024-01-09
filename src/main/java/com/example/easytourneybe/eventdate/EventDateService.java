package com.example.easytourneybe.eventdate;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.eventdate.dto.EventDateAdditionalDto;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.match.Match;
import com.example.easytourneybe.match.MatchService;
import com.example.easytourneybe.match.interfaces.IMatchRepository;
import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.tournament.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    @Autowired
    IMatchRepository matchRepository;
    public List<EventDate> findAllByTournamentId(Integer tournamentId) {
        return eventDateRepository.findAllByTournamentId(tournamentId);
    }

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

    public ResponseEntity<ResponseObject> updateStarTimeAndEndTime(Integer tournamentId, Integer eventDateId, String startTime, String endTime) {
        String warningMessage = "";
        EventDate eventDate = eventDateRepository.findById(eventDateId).orElseThrow(
                () -> new NoSuchElementException("EventDate with id " + eventDateId + " does not exist"));
        //check if tournament is finished
        Tournament tournament= eventDateRepository.findTournamentByIdAndIsDeletedFalse(tournamentId).orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(Objects.equals(tournament.getStatus().toString(), "FINISHED")||Objects.equals(tournament.getStatus().toString(), "DISCARDED"))
            throw new InvalidRequestException("This tournament is finished or discarded");
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

        if(eventDate.getDate().equals(LocalDate.now()) && startTimeValid.isBefore(LocalTime.now())&&eventDate.getStartTime()!=startTimeValid){
            throw new InvalidRequestException("Start time must be after current time");
        }
        List< Match > matches = matchRepository.getAllByEventDateIdOrOrderByStartTime(eventDateId);
        long startTimeChange= Duration.between(eventDate.getStartTime(),startTimeValid).toMinutes();
        eventDate.setStartTime(startTimeValid);
        eventDate.setEndTime(endTimeValid);
        eventDate.setUpdatedAt(LocalDateTime.now());
        for (Match match : matches) {
            if ((match.getEndTime().plusMinutes(startTimeChange).isBefore(match.getEndTime()) ||match.getStartTime().plusMinutes(startTimeChange).isBefore(match.getStartTime())) && startTimeChange > 0) {
                throw new InvalidRequestException("Match time is out of range");
            }
            match.setStartTime(match.getStartTime().plusMinutes(startTimeChange));
            match.setEndTime(match.getEndTime().plusMinutes(startTimeChange));
            if(match.getStartTime().isAfter(eventDate.getEndTime()) || match.getEndTime().isAfter(eventDate.getEndTime())){
                warningMessage = "Time of event date is not enough for all matches, please change time of event date or change match duration of some matches";
            }

        }

        ResponseObject responseObject = new ResponseObject(
                true, 1, eventDateRepository.save(eventDate)
        );
        if (!warningMessage.isEmpty()){
            responseObject.setAdditionalData(java.util.Collections.singletonMap("warningMessage", warningMessage));
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    public List<EventDateAdditionalDto> findAllEventDatesAndCountMatch(Integer tournamentId) {
        return eventDateRepository.findAllEventDatesAndCountMatch(tournamentId);
    }

}
