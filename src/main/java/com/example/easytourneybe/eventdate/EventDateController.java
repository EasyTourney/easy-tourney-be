package com.example.easytourneybe.eventdate;

import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.eventdate.dto.UpdateTimeDto;
import com.example.easytourneybe.model.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tournament/{tournamentId}/eventDate")
public class EventDateController {
    @Autowired
    private EventDateService eventDateService;
    @PutMapping("/{eventDateId}")
    public ResponseEntity<ResponseObject> updateStarTimeAndEndTime(
            @PathVariable Integer tournamentId,
            @PathVariable Integer eventDateId,
            @RequestBody UpdateTimeDto updateTimeDto) {
        EventDate updateEventDate=eventDateService.updateStarTimeAndEndTime(tournamentId,eventDateId,updateTimeDto.getStartTime(),updateTimeDto.getEndTime());
        ResponseObject responseObject = new ResponseObject(
                true, 1, updateEventDate
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }
}
