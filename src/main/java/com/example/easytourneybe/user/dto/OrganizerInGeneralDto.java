package com.example.easytourneybe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerInGeneralDto {
    private Integer id;
    private String fullName;
    private String email;
}
