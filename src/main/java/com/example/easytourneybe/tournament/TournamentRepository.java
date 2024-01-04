package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer>, TournamentRepositoryCustom {

        @Query("SELECT c FROM tournament c WHERE c.id = :id AND c.isDeleted = false")
        Optional<Tournament> findTournamentByIdAndIsDeletedFalse(@Param("id") Integer id);
        @Query("SELECT t FROM tournament t WHERE t.id = :tournamentId AND t.isDeleted = false AND t.status != 'DISCARDED' AND t.status != 'FINISHED'")
        Optional<Tournament> findTournamentById(@Param("tournamentId") Integer tournamentId);

        @Query("""
                SELECT new com.example.easytourneybe.tournament.TournamentPlanDto(t.startTimeDefault, t.endTimeDefault, t.timeBetween, t.matchDuration)
                FROM tournament t
                WHERE t.id = :tournamentId AND t.isDeleted = false
        """)
        Optional<TournamentPlanDto> getPlanByTournamentId(Integer tournamentId);

        @Query("""
                SELECT t
                FROM tournament t
                JOIN EventDate ed on t.id = ed.tournamentId
                WHERE ed.date <= now() and t.status = 'READY'
                GROUP BY t.id
        """)
        List<Tournament> findTournamentReadyNeedToChangeToInProgress();

        @Query(value = """
        SELECT t.tournament_id, GREATEST(MAX(m.end_time), temp.end_time) AS max_end_time, temp.date
        FROM tournament t
        JOIN (
                SELECT tournament_id, ranked.EventDateId, ranked.date, ranked.end_time
                FROM (
                    SELECT t.tournament_id, ed.id AS EventDateId, ed.date, ed.end_time,
                                ROW_NUMBER() OVER (PARTITION BY t.tournament_id ORDER BY ed.date DESC) AS row_num
                    FROM tournament t
                    JOIN event_date ed ON t.tournament_id = ed.tournament_id
                    WHERE t.status = 'IN_PROGRESS'
                ) ranked
                WHERE row_num = 1
            ) AS temp ON temp.tournament_id = t.tournament_id
        LEFT JOIN match m ON m.event_date_id = temp.EventDateId
        WHERE temp.date <= now()
        GROUP BY t.tournament_id, temp.end_time, temp.date
        """, nativeQuery = true)
        List<Object[]> findTournamentInProgressNeedToChangeToFinished();


        @Query(value = """
        select temp.tournament_id, min(temp.date) date, temp.EventDateId firstEventDateId, temp.start_time
        from (
            SELECT tournament_id, ranked.EventDateId, ranked.date, ranked.start_time
            FROM (
                SELECT t.tournament_id, ed.id AS EventDateId, ed.date, ed.start_time,
                        ROW_NUMBER() OVER (PARTITION BY t.tournament_id ORDER BY ed.date asc) AS row_num
                  FROM tournament t
                  JOIN event_date ed ON t.tournament_id = ed.tournament_id
                WHERE t.status = 'NEED_INFORMATION'
            ) ranked
            WHERE row_num = 1) as temp
        left join match m on m.event_date_id = temp.EventDateId
        group by temp.tournament_id, temp.EventDateId, temp.start_time
        having count(m.id) = 0 and (min(temp.date) <= now() or (min(temp.date) = now() and temp.start_time < LOCALTIME))
       
        """, nativeQuery = true)
        List<Object[]> findTournamentNeedInformationNeedToChangeToFinished();


        @Query("""
        select t
        from tournament t
        where t.categoryId = :categoryId
        """)
        List<Tournament> findTournamentByCategoryId(@Param("categoryId") Integer categoryId);
}

