package com.example.easytourneybe.match.interfaces;

import com.example.easytourneybe.match.dto.LeaderBoardDto;
import com.example.easytourneybe.match.Match;
import com.example.easytourneybe.match.dto.MatchOfLeaderBoardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface IMatchRepository extends JpaRepository<Match, Integer> {
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


    @Query(value = """
            SELECT m.*
            FROM match m
            WHERE m.event_date_id = :eventDateId
            ORDER BY m.start_time
            """,nativeQuery = true)
    List<Match> getAllByEventDateId(Integer eventDateId);

    Match getById(Long matchId);


    @Query(value = """
            SELECT ed.date  as date ,
                   m.* as matches
            FROM match m 
            JOIN event_date ed ON m.event_date_id = ed.id 
            JOIN tournament t ON ed.tournament_id = t.tournament_id 
            WHERE t.tournament_id = :tournamentId AND m.team_one_id IS NOT NULL AND m.team_two_id IS NOT NULL
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
    @Query(value = """
        SELECT m.*
        FROM match m 
        JOIN event_date ed ON m.event_date_id = ed.id 
        JOIN tournament t ON ed.tournament_id = t.tournament_id 
        WHERE t.tournament_id = :tournamentId 
        AND( (m.team_one_id = :teamOneId AND m.team_two_id = :teamTwoId) OR(m.team_one_id=:teamTwoId AND m.team_two_id = :teamOneId)) """, nativeQuery = true)
    List<Match>findDuplicateMatch(Integer tournamentId, Long teamOneId, Long teamTwoId);
    @Query(value = """
        SELECT m.*
        FROM match m 
        JOIN event_date ed ON m.event_date_id = ed.id 
        WHERE m.start_time > :startTime AND m.event_date_id = :eventDateId
        ORDER BY m.start_time ASC
        """, nativeQuery = true)
    List<Match>getAllByEventDateIdOrOrderByStartTime(Integer eventDateId, LocalTime startTime);

    List<Match> findAllByEventDateId(Integer eventDateId);

    @Query(value = """
                SELECT NEW com.example.easytourneybe.match.dto.LeaderBoardDto(t.teamId, t.teamName, t.score,
                SUM(CASE WHEN t.teamId = m.teamOneId THEN m.teamOneResult ELSE m.teamTwoResult END)
                - SUM(CASE WHEN t.teamId = m.teamOneId THEN m.teamTwoResult ELSE m.teamOneResult END) AS the_diff,
                SUM(CASE WHEN t.teamId = m.teamOneId THEN m.teamOneResult ELSE m.teamTwoResult END) AS total_result,
                DENSE_RANK() OVER ( ORDER BY t.score DESC,
                    SUM(CASE WHEN t.teamId = m.teamOneId THEN m.teamOneResult ELSE m.teamTwoResult END) DESC,
                    SUM(CASE WHEN t.teamId = m.teamOneId THEN m.teamOneResult ELSE m.teamTwoResult END) DESC,
                    SUM(CASE WHEN t.teamId = m.teamOneId THEN m.teamOneResult ELSE m.teamTwoResult END) DESC) AS rank)
                FROM Team t
                JOIN Match m ON t.id IN (m.teamOneId, m.teamTwoId)
                JOIN EventDate e ON e.id = m.eventDateId
                WHERE t.tournamentId = :tournamentId
                GROUP BY t.teamId
                ORDER BY t.score DESC, the_diff DESC, total_result DESC, t.teamName ASC
            """)
    List<LeaderBoardDto> getLeaderBoard(Integer tournamentId);

    @Query(value = """
                SELECT NEW com.example.easytourneybe.match.dto.MatchOfLeaderBoardDto(m.id, m.teamOneId,
                    t1.teamName,
                    m.teamTwoId,
                    t2.teamName,
                    m.teamOneResult, m.teamTwoResult, e.date, m.startTime, m.endTime,
                    (CASE WHEN m.id = m.teamOneId
                        THEN
                            (CASE WHEN m.teamOneResult IS NULL
                                THEN -1
                                ELSE
                                    (CASE WHEN m.teamOneResult > m.teamTwoResult
                                        THEN m.teamOneId
                                        ELSE
                                            (CASE WHEN m.teamOneResult = m.teamTwoResult
                                                THEN 0
                                                ELSE m.teamTwoId
                                            END)
                                     END)
                            END)
                        ELSE
                            (CASE WHEN m.teamOneResult IS NULL
                                THEN -1
                                ELSE
                                    (CASE WHEN m.teamOneResult > m.teamTwoResult
                                        THEN m.teamOneId
                                        ELSE
                                            (CASE WHEN m.teamOneResult = m.teamTwoResult
                                                THEN 0
                                                ELSE m.teamTwoId
                                            END)
                                     END)
                            END)
                    END) AS team_win_id)
                FROM Match m
                JOIN EventDate e ON e.id = m.eventDateId
                JOIN Team t1 ON m.teamOneId = t1.teamId
                JOIN Team t2 ON m.teamTwoId = t2.teamId
                WHERE t1.tournamentId = :tournamentId
                GROUP BY m.id, e.date, t1.teamName, t2.teamName
                ORDER BY e.date DESC, m.startTime DESC
            """)
    List<MatchOfLeaderBoardDto> getMatchOfLeaderBoard(Integer tournamentId);
    @Query(value = """
        SELECT m1.*
        FROM match m1
        JOIN event_date ed1 ON m1.event_date_id = ed1.id 
        JOIN tournament t1 ON ed1.tournament_id = t1.tournament_id 
        WHERE t1.tournament_id = :tournamentId 
        AND EXISTS (
            SELECT 1
            FROM match m2
            JOIN event_date ed2 ON m2.event_date_id = ed2.id 
            JOIN tournament t2 ON ed2.tournament_id = t2.tournament_id 
            WHERE t2.tournament_id = :tournamentId 
            AND m1.id <> m2.id
            AND (
                (m1.team_one_id = m2.team_one_id AND m1.team_two_id = m2.team_two_id)
                OR
                (m1.team_one_id = m2.team_two_id AND m1.team_two_id = m2.team_one_id)
            )
        )
        """, nativeQuery = true)
    List<Match>findAllDuplicateMatchByTournamentId(Integer tournamentId);
    @Query(value = """
        SELECT m.*
        FROM match m 
        JOIN event_date ed ON m.event_date_id = ed.id 
        WHERE m.event_date_id = :eventDateId
        ORDER BY m.start_time ASC
        """, nativeQuery = true)
    List<Match>getAllByEventDateIdOrOrderByStartTime(Integer eventDateId);
}
