package com.example.easytourneybe.team;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long teamId;
    @Column(name = "name")
    private String teamName;
    @Column(name = "tournament_id")
    private Long tournamentId;
    @Column(name = "score")
    private Integer score;
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
