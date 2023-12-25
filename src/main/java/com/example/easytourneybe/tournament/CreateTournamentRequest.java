package com.example.easytourneybe.tournament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CreateTournamentRequest {
    @NotNull
    @Length(min = 2, max = 30, message = "Tournament title must be between 2 and 30 characters")
    private String title;
    @NotNull
    private Integer categoryId;
    private List<LocalDate> eventDates;
    private String description;
}
