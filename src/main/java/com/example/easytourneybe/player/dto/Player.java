package com.example.easytourneybe.player.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long playerId;

    @Column(name = "name", nullable = false)
    private String playerName;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "dob")
    private LocalDate dateOfBirth;

    @Column(name = "phone", columnDefinition = "VARCHAR(11) CHECK (LENGTH(phone) BETWEEN 10 AND 11)")
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void setDateOfBirth (String dateOfBirth) {
        if (dateOfBirth == null) {
            return;
        }
        try {
            this.dateOfBirth = LocalDate.parse(dateOfBirth.trim());
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Date of birth must be valid");
        }
    }
}
