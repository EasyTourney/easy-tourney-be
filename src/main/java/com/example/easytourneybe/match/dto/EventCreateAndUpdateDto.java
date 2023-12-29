package com.example.easytourneybe.match.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;


@AllArgsConstructor
@Setter
@Getter
@Builder
@NoArgsConstructor
public class EventCreateAndUpdateDto {
    @NotNull(message = "time duration must between 1 and 1440.")
    @Max(value = 1440, message = "time duration must between 1 and 1440.")
    @Min(value = 1, message = "time duration must between 1 and 1440.")
    private Integer timeDuration;
    @NotNull(message = "Title must not be null")
    @Length(min = 1, max = 30, message = "Title of Event must be between 1 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Title can not contain special character")
    private String title;

    public void setTitle(String title) {
        this.title = title.trim();
    }
}
