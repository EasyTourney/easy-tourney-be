package com.example.easytourneybe.team.interfaces;

import com.example.easytourneybe.category.Category;
import com.example.easytourneybe.team.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT team.teamId AS team_id, team.teamName AS team_name, COALESCE(COUNT(player.playerId), 0) AS player_count " +
            "FROM Team team " +
            "LEFT JOIN Player player ON team.teamId = player.teamId " +
            "WHERE team.tournamentId = :tournament_id " +
            "GROUP BY team.teamId, team.teamName " +
            "ORDER BY team.teamId ASC")
    List<Object[]> getAllTeamAndPlayerCount(@Param("tournament_id") Integer tournamentId, Pageable page);
    @Query("SELECT COUNT(team.teamId) FROM Team team WHERE team.tournamentId = :tournament_id")
    Long getTotalRecordsForTournament(@Param("tournament_id") Integer tournamentId);
    @Query("SELECT t FROM Team t WHERE LOWER(t.teamName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Team> findTeamsByName(@Param("keyword") String keyword);
}
