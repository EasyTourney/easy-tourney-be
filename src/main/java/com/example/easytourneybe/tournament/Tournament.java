package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentFormat;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tournament_id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status",  columnDefinition = "status_tournament")
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @Column(name = "match_duration")
    private Integer matchDuration;

    @Column(name = "time_between")
    private Integer timeBetween;

    @Column(name = "start_time_default")
    private LocalTime startTimeDefault;

    @Column(name = "end_time_default")
    private LocalTime endTimeDefault;

    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private TournamentFormat format;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "description")
    private String description;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        startTimeDefault = LocalTime.of(0, 0, 0);
        endTimeDefault = LocalTime.of(23, 59, 59);
        isDeleted = false;
    }
}
