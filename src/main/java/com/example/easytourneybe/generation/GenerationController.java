package com.example.easytourneybe.generation;

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



    @PostMapping(value = "/{tournamentId}")
    public ResponseEntity<?> generate(@PathVariable Integer tournamentId,
                                      @RequestBody(required = false) GenerationRequest request) {
        List<GenerationDto> generations = generationService.generate(tournamentId, request.getDuration(), request.getBetweenTime(), request.getEventDates());
        ResponseObject responseObject = new ResponseObject(true, generations.size(), generations);
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateGeneration(@RequestBody GenerationUpdateRequest generationUpdateRequest) {
        List<GenerationDto> updatedGeneration=generationService.updateGeneration(generationUpdateRequest.getMatchDto(),
                generationUpdateRequest.getEventDateIdSelected(), generationUpdateRequest.getStartTime(),generationUpdateRequest.getEndTime());
        ResponseObject responseObject = new ResponseObject(true,updatedGeneration.size(),updatedGeneration);
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }


}
