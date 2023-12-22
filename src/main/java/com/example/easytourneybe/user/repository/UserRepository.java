package com.example.easytourneybe.user.repository;


import com.example.easytourneybe.user.dto.OrganizerInGeneralDto;
import com.example.easytourneybe.user.dto.User;
import com.example.easytourneybe.user.dto.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);

    @Query("SELECT count(distinct u.id) " +
            "FROM User u " +
            "LEFT JOIN OrganizerTournament ot ON u.id = ot.userId " +
            "WHERE u.role = 'ORGANIZER' AND u.isDeleted = false " +
            "AND (LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR (LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%')))) ")
    long totalOrganizer(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role = 'ORGANIZER' AND u.isDeleted = false")
    Optional<User> findOrganizerById(@Param("id") Integer userId);

    @Query("SELECT u FROM User u WHERE u.email = (:email) AND u.isDeleted = false")
    User findExistEmail(String email);

    @Query("""
        SELECT NEW com.example.easytourneybe.user.dto.OrganizerInGeneralDto(u.id, CONCAT(u.firstName, ' ', u.lastName) AS full_name, u.email)
            FROM User u
            JOIN OrganizerTournament ot ON u.id = ot.userId
            WHERE ot.tournamentId = :tournamentId AND u.isDeleted = false
            """)
    List<OrganizerInGeneralDto> findOrganizerInGeneral(@Param("tournamentId") Integer tournamentId);

    @Query("""
        SELECT NEW com.example.easytourneybe.user.dto.UserDto(u.id, u.firstName, u.lastName, u.email, u.role)
            FROM User u
            JOIN OrganizerTournament ot ON u.id = ot.userId
            WHERE ot.tournamentId = :tournamentId AND u.isDeleted = false
            """)
    List<UserDto> findUserByTournamentId(Integer tournamentId);

    boolean existsByEmailAndIsDeletedFalse(String email);

    @Query("""
        SELECT u
            FROM User u
            JOIN OrganizerTournament ot ON u.id = ot.userId
            WHERE ot.tournamentId = :tournamentId AND ot.userId = :userId AND u.isDeleted = false
            """)
    User isOrganizerOfTournament(Integer userId, Integer tournamentId);

    @Query("""
        SELECT u
        FROM User u
        WHERE u.id = :id AND u.isDeleted = false
    """)
    Optional<User> findUserById(Integer id);
}
