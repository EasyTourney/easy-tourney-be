package com.example.easytourneybe.team.interfaces;

import com.example.easytourneybe.team.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT team.teamId AS team_id, team.teamName AS team_name, COUNT(player.playerId) AS player_count " +
            "FROM Team team " +
            "JOIN Player player ON team.teamId = player.teamId " +
            "GROUP BY team.teamId, team.teamName " +
            "ORDER BY team.teamId ASC")
    List<Object[]> getAllTeamAndPlayerCount(Pageable page);

}
