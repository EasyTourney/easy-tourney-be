package com.example.easytourneybe.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@AllArgsConstructor
@Data
@Builder
public class EventCreateAndUpdateDto {
    @NotNull
    @Max(1440)
    @Min(1)
    private Integer timeDuration;
    @NotNull
    @Length(min = 1, max = 30, message = "Title of Event must be between 1 and 30 characters")
    private String title;

    public void setTitle(String title) {
        this.title = title.trim();
    }
}
