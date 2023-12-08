package com.example.easytourneybe.user.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organizerTournament")
public class OrganizerTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "User id is required")
    @Column(name = "user_id")
    private Long userId;

    @NotNull(message = "Tournament id is required")
    @Column(name = "tournament_id")
    private Long tournamentId;

}
