package com.example.easytourneybe.user.repository;


import com.example.easytourneybe.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
    @Query("SELECT count(distinct u.id) " +
            "FROM User u " +
            "LEFT JOIN OrganizerTournament ot ON u.id = ot.userId " +
            "WHERE u.role = 'ORGANIZER' AND u.isDeleted = false " +
            "AND (LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    long totalOrganizer( @Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role = 'ORGANIZER' AND u.isDeleted = false")
    Optional<User> findOrganizerById(@Param("id") Integer userId);
}
