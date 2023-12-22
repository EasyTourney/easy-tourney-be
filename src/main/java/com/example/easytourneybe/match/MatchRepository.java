package com.example.easytourneybe.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    @Query(value = """
        SELECT 
            CASE WHEN COUNT(m) > 0 
                THEN TRUE 
                ELSE FALSE 
            END 
        FROM match m WHERE m.event_date_id = :eventDateId""", nativeQuery = true)
    boolean isHaveMatchInDate(Integer eventDateId);
}
