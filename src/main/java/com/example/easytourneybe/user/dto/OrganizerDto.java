package com.example.easytourneybe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerDto {

    private Integer id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Long totalTournament;
}
