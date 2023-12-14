package com.example.easytourneybe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerTableDto {

    private Integer id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Long totalTournament;
    private Date dateOfBirth;

}
