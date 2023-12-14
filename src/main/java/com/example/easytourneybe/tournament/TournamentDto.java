package com.example.easytourneybe.tournament;

import com.example.easytourneybe.category.CategoryDto;
import com.example.easytourneybe.enums.tournament.TournamentFormat;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.eventdate.EventDate;
import com.example.easytourneybe.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class TournamentDto {

    private Integer id;
    private String title;
    private CategoryDto category;
    private LocalDateTime createdAt;
    private TournamentStatus status;
    private Integer matchDuration;
    private TournamentFormat format;
    private List<UserDto> organizers;
    private List<EventDate> eventDates;
}
