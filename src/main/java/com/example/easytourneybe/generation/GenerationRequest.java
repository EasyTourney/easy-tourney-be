package com.example.easytourneybe.generation;

import com.example.easytourneybe.eventdate.EventDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationRequest {
    private Integer duration;
    private Integer betweenTime;
    private List<EventDate> eventDates;
}
