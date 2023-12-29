package com.example.easytourneybe.generation;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationRequest {

    @Min(value = 1, message = "Duration must be at least 1")
    private Integer duration;
    @Min(value = 0, message = "Duration must be at least 0")
    private Integer betweenTime;

    private LocalTime startTime;

    private LocalTime endTime;

    @AssertTrue(message = "Start time must be greater than 00:00:00")
    public boolean isStartTimeValid() {
        return startTime.isAfter(LocalTime.MIDNIGHT);
    }

    @AssertTrue(message = "End time must be a valid time and less than 23:59:59")
    public boolean isEndTimeValid() {
        return endTime.isBefore(LocalTime.of(23, 59, 59));
    }
    @AssertTrue(message = "Start time must be before end time")
    public boolean isTimeRangeValid() {
        return startTime.isBefore(endTime);
    }

}
