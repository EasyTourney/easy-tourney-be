package com.example.easytourneybe.organizer_tournament;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "organizer_tournament")
public class OrganizerTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "User id is required")
    @Column(name = "user_id")
    private Integer userId;

    @NotNull(message = "Tournament id is required")
    @Column(name = "tournament_id")
    private Integer tournamentId;

}
