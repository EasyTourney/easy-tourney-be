package com.example.easytourneybe.eventdate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventDateAdditionalDto {
    private Integer id;
    private Long numMatch;
}
