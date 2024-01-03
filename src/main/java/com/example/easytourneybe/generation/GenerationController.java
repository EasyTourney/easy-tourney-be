package com.example.easytourneybe.generation;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.generation.interfaces.IGenerationService;
import com.example.easytourneybe.model.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/generate")
public class GenerationController {

    @Autowired
    private IGenerationService generationService;

    @GetMapping("/{tournamentId}")
    public ResponseEntity<?> getAllGeneration(@PathVariable Integer tournamentId){
        List<GenerationDto> generations= generationService.getAllGeneration(tournamentId);
        ResponseObject responseObject = new ResponseObject(true, generations.size(), generations);
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    @PostMapping(value = "/{tournamentId}")
    public ResponseEntity<?> generate(@PathVariable Integer tournamentId,
                                      @RequestBody(required = false) GenerationRequest request) {

        if( request.isTimeRangeValid()) {
            List<GenerationDto> generations = generationService.generate(tournamentId, request.getDuration(), request.getBetweenTime(), request.getStartTime(), request.getEndTime());
            ResponseObject responseObject = new ResponseObject(true, generations.size(), generations);
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        }
        else {
            throw new InvalidRequestException("Start time or end time invalid");
        }
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateGeneration(@RequestBody GenerationUpdateRequest generationUpdateRequest) {
        List<GenerationDto> updatedGeneration = generationService.updateGeneration(generationUpdateRequest.getMatchId(),
                generationUpdateRequest.getEventDateIdSelected(), generationUpdateRequest.getMatchOfNewTimeId());
        ResponseObject responseObject = new ResponseObject(true, updatedGeneration.size(), updatedGeneration);
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }


}
