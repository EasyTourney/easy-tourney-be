package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer>, TournamentRepositoryCustom {

        Page<Tournament> findAllByStatusAndTitleContaining(TournamentStatus status, String infix, Pageable pageable);
        Page<Tournament> findAllByTitleContaining(String infix, Pageable pageable);
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

}

