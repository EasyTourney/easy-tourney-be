package com.example.easytourneybe.team.interfaces;

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
            "ORDER BY team.createdAt DESC")
    List<Object[]> getAllTeamAndPlayerCount(@Param("tournament_id") Integer tournamentId, Pageable page);

    @Query("SELECT COUNT(team.teamId) FROM Team team WHERE team.tournamentId = :tournament_id")
    Long getTotalRecordsForTournament(@Param("tournament_id") Integer tournamentId);
    @Query("SELECT team.teamId FROM Team team")
    List<Long> getAllTeamID();
    @Query("SELECT t FROM Team t WHERE (t.teamName) = (:keyword) AND t.tournamentId = :tournament_id")
    List<Team> findTeamsByName(@Param("tournament_id") Integer tournament_id, @Param("keyword") String keyword);

    @Query("SELECT t FROM Team t " +
            "JOIN tournament tm ON t.tournamentId = tm.id " +
            "WHERE t.tournamentId = :tournamentId AND t.teamId = :id " +
            "AND (tm.isDeleted = false AND tm.status != 'DISCARDED')")
    Optional<Team> findTeamById(@Param("tournamentId") Integer tournamentId, @Param("id") Long id);

    void deleteByTournamentIdAndTeamId(Integer tournamentId, Long teamId);

    @Query("SELECT team FROM Team team WHERE team.tournamentId = :tournament_id")
    List<Team> findTeamByTournamentId(@Param("tournament_id") Integer tournamentId);
    Team getTeamByTeamId(Long id);
    @Query("SELECT team.teamName FROM Team team WHERE team.teamId = :id")
    String getTeamNameByTeamId(Long id);
}