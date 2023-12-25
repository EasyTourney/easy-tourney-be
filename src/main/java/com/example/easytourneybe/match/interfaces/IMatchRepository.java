package com.example.easytourneybe.match.interfaces;

import com.example.easytourneybe.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IMatchRepository extends JpaRepository<Match,Integer> {
    @Query(value = """
        SELECT 
            CASE WHEN COUNT(m) > 0 
                THEN TRUE 
                ELSE FALSE 
            END 
        FROM match m WHERE m.event_date_id = :eventDateId""", nativeQuery = true)
    boolean isHaveMatchInDate(Integer eventDateId);

    @Query(value = """
            SELECT 
                CASE WHEN COUNT(m) > 0 
                    THEN TRUE 
                    ELSE FALSE 
                END 
            FROM match m 
            JOIN event_date ed on m.event_date_id = ed.id
            WHERE ed.tournament_id = :tournamentId""", nativeQuery = true)
    boolean isHaveMatchInTournament(Integer tournamentId);

    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM match m
            USING event_date ed
            WHERE m.event_date_id = ed.id
            AND ed.tournament_id = :tournamentId""", nativeQuery = true)
    void deleteMatchByTournamentId(Integer tournamentId);

    List<Match> getAllByEventDateId(Integer eventDateId);

    Match getMatchByIdIs(Long matchId);



    @Query(value = """
        SELECT ed.date  as date ,
               m.* as matches
        FROM match m 
        JOIN event_date ed ON m.event_date_id = ed.id 
        JOIN tournament t ON ed.tournament_id = t.tournament_id 
        WHERE t.tournament_id = :tournamentId
        ORDER BY ed.date DESC""", nativeQuery = true)
    List<Object[]> getAllResult(Integer tournamentId);

    @Query(value = """
        SELECT 
            CASE WHEN COUNT(m) > 0 
                THEN TRUE 
                ELSE FALSE 
            END 
        FROM match m 
        JOIN event_date ed ON m.event_date_id = ed.id 
        JOIN tournament t ON ed.tournament_id = t.tournament_id 
        WHERE t.tournament_id = :tournamentId AND m.id = :matchID""", nativeQuery = true)
    boolean isMatchInTournament(Integer tournamentId, Integer matchID);


}
