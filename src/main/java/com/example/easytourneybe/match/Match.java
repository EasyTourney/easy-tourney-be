package com.example.easytourneybe.match;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="match")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "team_one_id")
    private Integer teamOneId;

    @Column(name = "team_two_id")
    private Integer teamTwoId;

    @Column(name = "team_one_result")
    private Integer teamOneResult;

    @Column(name = "team_two_result")
    private Integer teamTwoResult;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "event_date_id")
    private Integer eventDateId;
}
