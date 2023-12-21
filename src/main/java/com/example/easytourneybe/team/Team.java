package com.example.easytourneybe.team;

import com.example.easytourneybe.util.RegexpUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
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
    @Pattern(regexp = RegexpUtils.CATEGORY_REGEXP, message = "Category name must be alphanumeric")
    @Length(min = 2, max = 30, message = "Category name must be between 2 and 30 characters")
    @Column(name = "name")
    private String teamName;
    @Column(name = "tournament_id")
    private Integer tournamentId;
    @Column(name = "score")
    private Integer score;
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public void setTeamName(String teamName) {
        this.teamName = (teamName != null) ? teamName.trim() : null;
    }
}
