package com.example.easytourneybe.match;

import com.example.easytourneybe.enums.match.TypeMatch;
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
@Table(name = "match")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "team_one_id")
    private Long teamOneId;

    @Column(name = "team_two_id")
    private Long teamTwoId;

    @Column(name = "team_one_result")
    private Integer teamOneResult;

    @Column(name = "team_two_result")
    private Integer teamTwoResult;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "duration")
    private Integer matchDuration;

    @Column(name = "event_date_id")
    private Integer eventDateId;

    @Column(name = "title")
    private String title;

    @Column(name = "type", columnDefinition = "types")
    @Enumerated(EnumType.STRING)
    private TypeMatch type;


    public Object clone()  {
        Match match=new Match();
        match.setId(this.id);
        match.setStartTime(this.startTime);
        match.setEndTime(this.endTime);
        match.setTeamOneId(this.teamOneId);
        match.setTeamTwoId(this.teamTwoId);
        match.setMatchDuration(this.matchDuration);
        match.setTeamOneResult(this.teamOneResult);
        match.setTeamTwoResult(this.teamTwoResult);
        match.setEventDateId(this.eventDateId);
        match.setTitle(this.title);
        match.setType(this.type);
        return match;
    }
}
