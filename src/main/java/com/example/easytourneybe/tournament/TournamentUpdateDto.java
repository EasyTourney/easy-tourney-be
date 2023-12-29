package com.example.easytourneybe.tournament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentUpdateDto {
    private Integer id;
    
    @Length(max = 30, message = "Title must be less than 30 characters")
    private String title;

    @Length(max = 100, message = "Description must be less than 100 characters")
    private String description;

    private Integer categoryId;

    private List<Integer> organizers;

    private List<LocalDate> eventDates;

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setOrganizers(List<Integer> organizers) {
        try {
            this.organizers = organizers.stream().distinct().toList();
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Request invalid data");
        }
    }

    public void setEventDates(List<LocalDate> eventDates) {
        try {
            this.eventDates = eventDates.stream().distinct().toList();
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Invalid date format");
        }
    }

    public static void validateRequest(TournamentUpdateDto request) {

        if (request.getTitle() == null
            && request.getDescription() == null
            && request.getCategoryId() == null
            && request.getOrganizers() == null
            && request.getEventDates() == null) {

            throw new HttpMessageNotReadableException("Request invalid data");
        }

    }
}

