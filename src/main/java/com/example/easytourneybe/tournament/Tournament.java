package com.example.easytourneybe.tournament;

import com.example.easytourneybe.category.Category;
import com.example.easytourneybe.enums.tournament.TournamentFormat;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.user.dto.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status",  columnDefinition = "status_tournament")
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @Column(name = "match_duration")
    private Integer matchDuration;

    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private TournamentFormat format;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany
    @JoinTable(
            name = "organizer_tournament",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
}
