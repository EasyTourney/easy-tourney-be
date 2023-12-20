package com.example.easytourneybe.tournament;
import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.user.UserService;
import com.example.easytourneybe.category.Category;
import com.example.easytourneybe.category.interfaces.CategoryService;
import com.example.easytourneybe.enums.tournament.TournamentFormat;
import com.example.easytourneybe.eventdate.EventDate;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventDateService eventDateService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    EntityManager entityManager;

    private final String INSERT_INTO_TOURNAMENT_ORGANIZER_TABLE =
            "INSERT INTO organizer_tournament(user_id, tournament_id) VALUES (:userId, :tournamentId)";
    public ResponseObject getAll(Integer page, Integer pageSize, String field, String sortType, TournamentStatus status, String search, Integer categoryId) {
        search = search.replaceAll("%", "\\\\%");
        search = search.replaceAll("_", "\\\\_");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false, isOrganizer = false;
        for (Object o : authentication.getAuthorities()) {
            if (UserRole.ADMIN.name().equals(o.toString())) isAdmin = true;
            if (UserRole.ORGANIZER.name().equals(o.toString())) isOrganizer = true;
        }
        ResponseObject results;
        if(isAdmin) {
            results = tournamentRepository.findAllByUserId(null, page, pageSize, sortType, field, status, search, categoryId);
       } else if (isOrganizer) {
            Integer userId = userService.findByEmail(authentication.getName()).getId();
            results = tournamentRepository.findAllByUserId(userId, page, pageSize, sortType, field, status, search, categoryId);
        } else {
            return null;
        }
        return results;
    }
    public Optional<Tournament> deleteTournament(Integer id) {
        Optional<Tournament> foundTournament = tournamentRepository.findTournamentByIdAndIsDeletedFalse(id);
        if (foundTournament.isPresent()) {
            Tournament tournament = foundTournament.get();
            tournament.setIsDeleted(true);
            tournament.setDeletedAt(LocalDateTime.now());
            tournament.setStatus(TournamentStatus.DELETED);
            tournamentRepository.save(tournament);
            return Optional.of(tournament);
        } else {
            throw new NoSuchElementException("Tournament not found");
        }
    }


    public ResponseObject getTournamentToShowGeneral(Integer id) {
        return tournamentRepository.findTournamentToShowGeneral(id);
    }

    @Transactional
    public Tournament createTournament(String title, Integer categoryId, List<LocalDate> eventDates) {
        Optional<Category> categoryOpt = categoryService.findCategoryById(Long.valueOf(categoryId));
        if (!categoryOpt.isPresent() || categoryOpt.get().isDeleted()) throw  new NoSuchElementException("category not found with id: " + categoryId);
        Integer defaultMatchDuration = 60;
        Tournament tournament = Tournament.builder().title(title).categoryId(categoryId)
                .format(TournamentFormat.ROUND_ROBIN)
                .status(TournamentStatus.NEED_INFORMATION)
                .matchDuration(defaultMatchDuration)
                .build();
        tournament = tournamentRepository.save(tournament);
        final Integer tournamentId = tournament.getId();
        List<EventDate> events = null;
        if (eventDates != null) {
            events = eventDates.stream().map(date -> {
                return EventDate.builder().tournamentId(tournamentId)
                        .date(date)
                        .startTime(LocalTime.MIN)
                        .endTime(LocalTime.of(23,59,59))
                        .build();
            }).toList();
        }
        if (events != null) eventDateService.saveAll(events);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = userService.findByEmail(authentication.getName()).getId();
        entityManager.createNativeQuery(INSERT_INTO_TOURNAMENT_ORGANIZER_TABLE)
                .setParameter("userId", userId)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
        return tournament;
    }
}
