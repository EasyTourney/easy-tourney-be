package com.example.easytourneybe.match;

import com.example.easytourneybe.generation.GenerationDto;
import com.example.easytourneybe.match.dto.EventCreateAndUpdateDto;
import com.example.easytourneybe.match.interfaces.EventService;
import com.example.easytourneybe.model.ResponseObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/tournament/{tournamentId}/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;


    @PostMapping("/{eventDateId}")
    public ResponseEntity<?> createEvent(@PathVariable Integer eventDateId,
                                         @Valid @RequestBody EventCreateAndUpdateDto evtCreateDto,
                                         @PathVariable Integer tournamentId) {
        var result = eventService.createEvent(eventDateId, evtCreateDto, tournamentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .success(true)
                        .total(1)
                        .data(result)
                        .build()
        );
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer eventId, @PathVariable Integer tournamentId) {
        GenerationDto result = eventService.deleteEvent(eventId, tournamentId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .success(true)
                        .total(result.getMatches().size())
                        .data(result)
                        .build()
        );
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Integer eventId,
                                         @Valid @RequestBody EventCreateAndUpdateDto eventDto,
                                         @PathVariable Integer tournamentId) {
        GenerationDto result = eventService.updateEvent(eventId, eventDto, tournamentId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .success(true)
                        .total(result.getMatches().size())
                        .data(result)
                        .build()
        );
    }
}
