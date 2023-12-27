package com.example.easytourneybe.match;

import com.example.easytourneybe.match.dto.EventCreateAndUpdateDto;
import com.example.easytourneybe.match.interfaces.EventService;
import com.example.easytourneybe.model.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/{eventDateId}")
    public ResponseEntity<?> createEvent(@PathVariable Integer eventDateId, @RequestBody EventCreateAndUpdateDto evtCreateDto) {
        var result = eventService.createEvent(eventDateId, evtCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .success(true)
                        .total(1)
                        .data(result)
                        .build()
        );
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .success(true)
                        .total(1)
                        .build()
        );
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Integer eventId, @RequestBody EventCreateAndUpdateDto eventDto) {
        List<MatchDto> result = eventService.updateEvent(eventId, eventDto);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .success(true)
                        .total(result.size())
                        .data(result)
                        .build()
        );
    }
}
