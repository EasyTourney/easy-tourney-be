package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusRequest {
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;
}
