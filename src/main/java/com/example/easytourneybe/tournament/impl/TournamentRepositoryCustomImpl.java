package com.example.easytourneybe.tournament.impl;
import com.example.easytourneybe.category.CategoryDto;
import com.example.easytourneybe.enums.tournament.TournamentFormat;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.eventdate.EventDate;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.tournament.TournamentDto;
import com.example.easytourneybe.tournament.TournamentRepositoryCustom;
import com.example.easytourneybe.user.dto.UserDto;
import com.example.easytourneybe.user.UserService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class TournamentRepositoryCustomImpl implements TournamentRepositoryCustom {
    @Autowired
    EntityManager entityManager;

    @Autowired
    EventDateService eventDateService;

    @Autowired
    UserService userService;

    private final String GET_TOURNAMENT_BY_USERID =
            "SELECT t.tournament_id tourmamentId, title, c.category_id, category_name category,       " +
            "       t.created_at createdAt, status, match_duration matchDuration, format            \n" +
            "FROM  tournament t                                                                     \n" +
            "   JOIN organizer_tournament ot on t.tournament_id = ot.tournament_id                  \n" +
            "   JOIN category c on c.category_id = t.category_id                                    \n" +
            "WHERE %s and %s and %s                                                                 \n" +
           "GROUP BY t.tournament_id, title, c.category_id, category_name,                            " +
                    "t.created_at, status, match_duration, format                                   \n" +
            "ORDER BY %s %s                                                                         \n" +
            "LIMIT :pageSize                                                                        \n" +
            "OFFSET :off_set";
    @Override
    public List<TournamentDto> findAllByUserId(Integer userId, Integer page, Integer pageSize, String sortType, String field, TournamentStatus status, String search) {
        String userIdFilter = "true";
        if (userId != null) userIdFilter = "ot.user_id=" + userId;
        String statusFilter = "true";
        if (status != null) statusFilter = "status='" + status + "'";
        String searchFilter = "true";
        if (!search.equals("")) searchFilter = "(t.title LIKE '%" + search + "%' OR c.category_name LIKE '%" + search + "%')";
        String sql = String.format(GET_TOURNAMENT_BY_USERID, userIdFilter, statusFilter, searchFilter, field, sortType);
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("pageSize", pageSize)
                .setParameter("off_set", page*pageSize)
                .getResultList();

        return rows.stream().map(row -> {
            Timestamp timestamp = (Timestamp) row[4];
            Integer tournamentId = (Integer) row[0];
            List<EventDate> eventDates = eventDateService.findAllByTournamentId(tournamentId);
            List<UserDto> userDTOs = userService.findUserByTournamentId(tournamentId);
            return TournamentDto.builder()
                    .id(tournamentId)
                    .title((String) row[1])
                    .category(CategoryDto.builder().id((Long) row[2]).name((String) row[3]).build())
                    .createdAt(timestamp.toLocalDateTime())
                    .status(TournamentStatus.valueOf((String) row[5]))
                    .matchDuration((Integer) row[6])
                    .format(TournamentFormat.valueOf((String) row[7]))
                    .eventDates(eventDates)
                    .organizers(userDTOs)
                    .build();
            }).toList();
    }
}
