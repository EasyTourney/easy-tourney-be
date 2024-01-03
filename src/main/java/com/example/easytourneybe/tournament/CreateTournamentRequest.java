package com.example.easytourneybe.tournament;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTournamentRequest {

    @Pattern(regexp = "^[a-zA-Z0-9\\p{L}\\s]*$", message = "Title can not contain special character")
    @Length(max = 50, min = 2, message = "Tournament title must be between 2 and 50 characters")
    private String title;
    @NotNull(message = "category id must not be null.")
    private Integer categoryId;
    @NotNull(message = "EventDate must not be null.")
    private Set<LocalDate> eventDates;
    private String description;

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setEventDates(Set<LocalDate> eventDates) {
        this.eventDates = eventDates;
        if (eventDates.isEmpty())
            this.eventDates = null;
    }
}
