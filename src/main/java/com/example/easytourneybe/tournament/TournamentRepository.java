package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer>, TournamentRepositoryCustom {

        Page<Tournament> findAllByStatusAndTitleContaining(TournamentStatus status, String infix, Pageable pageable);
        Page<Tournament> findAllByTitleContaining(String infix, Pageable pageable);
}

