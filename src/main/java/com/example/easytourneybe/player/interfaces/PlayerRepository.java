package com.example.easytourneybe.player.interfaces;

import com.example.easytourneybe.player.dto.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p " +
            "FROM Player p " +
            "WHERE p.teamId = :teamId " +
            "ORDER BY p.createdAt desc ")
    List<Player> getAllPlayersByTeamID(@Param("teamId") Long teamId);
    @Query("SELECT COUNT(p.playerId) FROM Player p WHERE p.teamId = :teamId")
    Long getTotalPlayers(@Param("teamId") Long teamId);
    Player findByPlayerIdAndTeamId(Long playerId, Long teamId);
}
