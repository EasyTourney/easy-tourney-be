package com.example.easytourneybe.user.repository;

import com.example.easytourneybe.user.dto.OrganizerTableDto;
import com.example.easytourneybe.user.transformer.OrganizerTransformer;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;

import java.util.List;

@AllArgsConstructor
public class UserDao {
    private SessionFactory sessionFactory;

    public List<OrganizerTableDto> organizerTable(String keyword, String sortValue, String sortType, int page, int size) {
        Session session = sessionFactory.openSession();

        String query = "SELECT " +
                "    u.id, " +
                "    u.email, " +
                "    CONCAT(u.first_name, ' ', u.last_name) AS fullName, " +
                "    u.phone_number, " +
                "    u.created_at, " +
                "   COUNT(ot.tournament_id) AS totalTournament ," +
                "   u.date_of_birth " +
                "FROM " +
                "    users u " +
                "LEFT JOIN " +
                "    organizer_tournament ot ON u.id = ot.user_id " +
                "WHERE " +
                "    u.role = 'ORGANIZER' AND u.is_deleted = false " +
                "    AND (LOWER(u.email) LIKE LOWER(:keyword) " +
                "    OR LOWER(CONCAT(u.first_name, ' ', u.last_name)) LIKE LOWER(:keyword)) " +
                "GROUP BY " +
                "    u.id " +
                "ORDER BY " +
                sortValue + " " + sortType;

        var result = session.createNativeQuery(query)
                .setParameter("keyword", "%" + keyword + "%")
                .unwrap(NativeQuery.class)
                .addScalar("id", StandardBasicTypes.INTEGER)
                .addScalar("email", StandardBasicTypes.STRING)
                .addScalar("fullName", StandardBasicTypes.STRING)
                .addScalar("phone_number", StandardBasicTypes.STRING)
                .addScalar("created_at", StandardBasicTypes.TIMESTAMP)
                .addScalar("totalTournament", StandardBasicTypes.LONG)
                .addScalar("date_of_birth", StandardBasicTypes.DATE)
                .setResultTransformer(new OrganizerTransformer())
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        session.close();
        return result;
    }

}


