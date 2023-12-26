package com.example.easytourneybe.tournament.impl;

import com.example.easytourneybe.category.CategoryDto;
import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.enums.tournament.TournamentFormat;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.tournament.TournamentDto;
import com.example.easytourneybe.tournament.TournamentGeneralDto;
import com.example.easytourneybe.tournament.TournamentRepositoryCustom;
import com.example.easytourneybe.user.UserService;
import com.example.easytourneybe.user.dto.OrganizerInGeneralDto;
import com.example.easytourneybe.user.dto.UserDto;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class TournamentRepositoryCustomImpl implements TournamentRepositoryCustom {
    @Autowired
    EntityManager entityManager;

    @Autowired
    EventDateService eventDateService;

    @Autowired
    UserService userService;

    private final String GET_TOURNAMENT_BY_USERID =
            "SELECT t.tournament_id tourmamentId, title, c.category_id, category_name category,      \n" +
                    "       t.created_at createdAt, status, match_duration matchDuration, format,            \n" +
                    "       (SELECT COUNT(distinct ot.tournament_id)                                         \n" +
                    "               FROM organizer_tournament ot                                             \n" +
                    "               JOIN tournament t ON t.tournament_id = ot.tournament_id                  \n" +
                    "            WHERE %s and %s and %s and %s and t.is_deleted != true ) AS total_records   \n" +
                    "FROM  tournament t                                                                      \n" +
                    "   JOIN organizer_tournament ot on t.tournament_id = ot.tournament_id                   \n" +
                    "   JOIN category c on c.category_id = t.category_id                                     \n" +
                    "WHERE %s and %s and %s and %s and t.is_deleted != true                                  \n" +
                    "GROUP BY t.tournament_id, title, c.category_id, category_name,                          \n" +
                    "         t.created_at, status, match_duration, format                                   \n" +
                    "ORDER BY %s %s                                                                          \n" +
                    "LIMIT :pageSize                                                                         \n" +
                    "OFFSET :off_set";

    private final String GET_TOURNAMENT_GENERAL_INFO = """
            SELECT t.tournament_id, t.title, t.description, t.status, c.category_id, c.category_name
                    FROM tournament t
                    JOIN category c on t.category_id = c.category_id
                    WHERE t.tournament_id = :id AND t.is_deleted = false""";


    @Override
    public ResponseObject findAllByUserId(Integer userId, Integer page, Integer pageSize, String sortType, String field, TournamentStatus status, String search, Integer categoryId) {
        String userIdFilter = "true";
        if (userId != null) userIdFilter = "user_id=" + userId;
        String statusFilter = "true";
        if (status != null) statusFilter = "status='" + status + "'";
        String searchFilter = "true";
        if (!search.equals("")) searchFilter = "(t.title LIKE '%" + search + "%')";
        String categoryIdFilter = "true";
        if (categoryId != null) categoryIdFilter = "t.category_id = " + categoryId;
        String sql = String.format(GET_TOURNAMENT_BY_USERID, userIdFilter, statusFilter, searchFilter, categoryIdFilter,
                                                            userIdFilter, statusFilter, searchFilter, categoryIdFilter,
                                                            field, sortType);

        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("pageSize", pageSize)
                .setParameter("off_set", page*pageSize)
                .getResultList();

        if (rows.isEmpty()) return ResponseObject.builder().total(0).success(true).build();

        Long totalLong = (Long) rows.get(0)[8];
        String totalStr = String.valueOf(totalLong);
        Integer total = Integer.valueOf(totalStr);
        List<TournamentDto> data =
                rows.stream().map(row -> {
                    Timestamp timestamp = (Timestamp) row[4];
                    Integer tournamentId = (Integer) row[0];
                    List<EventDate> eventDates = eventDateService.findAllByTournamentId(tournamentId);
                    List<UserDto> userDTOs = userService.findUserByTournamentId(tournamentId);
                    return TournamentDto.builder()
                            .id(tournamentId)
                            .title((String) row[1])
                            .category(CategoryDto.builder().id((Long) row[2]).categoryName((String) row[3]).build())
                            .createdAt(timestamp.toLocalDateTime())
                            .status(TournamentStatus.valueOf((String) row[5]))
                            .matchDuration((Integer) row[6])
                            .format(TournamentFormat.valueOf((String) row[7]))
                            .eventDates(eventDates)
                            .organizers(userDTOs)
                            .build();
                }).toList();
        return ResponseObject.builder()
                .data(data)
                .total(data.size())
                .success(true)
                .additionalData(Map.of("totalTournament", total))
                .build();
    }

    @Override
    public ResponseObject findTournamentToShowGeneral(Integer tournament_id) {
        String sql = String.format(GET_TOURNAMENT_GENERAL_INFO, tournament_id);
        List<?> rows = entityManager.createNativeQuery(sql).setParameter("id", tournament_id).getResultList();
        if (rows.isEmpty()) throw new InvalidRequestException("Tournament not found");

        /*
        Check permission of user
            * If user is ADMIN -> pass
            * If user is ORGANIZER -> check if user is organizer of this tournament
        */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!userService.isExistUser(authentication.getName())) {
            throw new NoSuchElementException("User not found");
        }

        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(UserRole.ADMIN.name()))) {
            if (!userService.isOrganizerOfTournament(authentication.getName(), tournament_id)) {
                throw new NoSuchElementException("You don't have permission to view this tournament");
            }
        }

        //Fill data to TournamentGeneralDto
        Object[] row = (Object[]) rows.get(0);
        List<EventDate> eventDates = eventDateService.findAllByTournamentId(tournament_id);
        List<OrganizerInGeneralDto> userDTOs = userService.findOrganizerInGeneral(tournament_id);
        TournamentGeneralDto tournamentGeneralDto = TournamentGeneralDto.builder()
                .id((Integer) tournament_id)
                .title((String) row[1])
                .description((String) row[2])
                .status(TournamentStatus.valueOf((String) row[3]))
                .category(CategoryDto.builder().id((Long) row[4]).categoryName((String) row[5]).build())
                .organizers(userDTOs)
                .eventDates(eventDates)
                .build();
        return ResponseObject.builder()
                .data(tournamentGeneralDto)
                .success(true)
                .total(1)
                .build();
    }
}
