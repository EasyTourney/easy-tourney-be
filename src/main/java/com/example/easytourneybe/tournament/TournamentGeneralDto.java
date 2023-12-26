package com.example.easytourneybe.tournament;

import com.example.easytourneybe.category.CategoryDto;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.user.dto.OrganizerInGeneralDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class TournamentGeneralDto {
        private Integer id;
        private String title;
        private String description;
        private TournamentStatus status;
        private CategoryDto category;
        private List<OrganizerInGeneralDto> organizers;
        private List<EventDate> eventDates;
}
