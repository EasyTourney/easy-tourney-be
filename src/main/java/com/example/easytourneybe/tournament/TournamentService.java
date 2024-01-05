package com.example.easytourneybe.tournament;

import com.example.easytourneybe.category.Category;
import com.example.easytourneybe.category.interfaces.CategoryService;
import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.enums.tournament.TournamentFormat;
import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.eventdate.dto.EventDate;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.match.dto.LeaderBoardDetailDto;
import com.example.easytourneybe.match.dto.LeaderBoardDto;
import com.example.easytourneybe.match.dto.MatchOfLeaderBoardDto;
import com.example.easytourneybe.match.MatchService;
import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.organizer_tournament.OrganizerTournament;
import com.example.easytourneybe.organizer_tournament.OrganizerTournamentService;
import com.example.easytourneybe.player.PlayerService;
import com.example.easytourneybe.team.TeamService;
import com.example.easytourneybe.user.UserService;
import com.example.easytourneybe.user.dto.User;
import com.example.easytourneybe.util.AuthenticationUtil;
import com.example.easytourneybe.util.DateValidatorUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.easytourneybe.util.TournamentStatusPermission.*;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    TeamService teamService;

    @Autowired
    private EventDateService eventDateService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private OrganizerTournamentService organizerTournamentService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

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
        if (isAdmin) {
            results = tournamentRepository.findAllByUserId(null, page, pageSize, sortType, field, status, search, categoryId);
        } else if (isOrganizer) {
            Integer userId = userService.findByEmail(authentication.getName()).getId();
            results = tournamentRepository.findAllByUserId(userId, page, pageSize, sortType, field, status, search, categoryId);
        } else {
            return null;
        }
        return results;
    }

    @Transactional
    public Optional<Tournament> deleteTournament(Integer id) {
        Optional<Tournament> foundTournament = tournamentRepository.findTournamentByIdAndIsDeletedFalse(id);

        User user = AuthenticationUtil.getCurrentUser(userService);
        var organizerTournaments = organizerTournamentService.findAllByTournamentId(id);
        boolean isHasPermission = false;
        for (OrganizerTournament organizerTournament : organizerTournaments) {
            if (organizerTournament.getUserId().equals(user.getId()))
                isHasPermission = true;
        }
        if (!isHasPermission)
            throw new NoSuchElementException("Tournament not found");

        if (foundTournament.isPresent()) {
            Tournament tournament = foundTournament.get();
            tournament.setIsDeleted(true);
            tournament.setDeletedAt(LocalDateTime.now());
            tournament.setStatus(TournamentStatus.DELETED);
            matchService.deleteAllByTournamentId(tournament.getId());
            playerService.deleteAllPlayerByTournamentId(tournament.getId());
            teamService.deleteTeamByTournamentId(id);
            eventDateService.deleteAllByTournamentId(tournament.getId());
            organizerTournamentService.deleteAllByTournamentId(tournament.getId());
            tournamentRepository.save(tournament);
            return Optional.of(tournament);
        } else {
            throw new NoSuchElementException("Tournament not found");
        }
    }


    public ResponseObject getTournamentToShowGeneral(Integer id) {
        ResponseObject response = tournamentRepository.findTournamentToShowGeneral(id);
        response.setAdditionalData(
            Map.of(
                "matchOfEventDates", eventDateService.findAllEventDatesAndCountMatch(id),
                "tournamentPlan", getPlanByTournamentId(id)
            )
        );
        return response;
    }

    @Transactional
    public Tournament createTournament(String title, Integer categoryId, Set<LocalDate> eventDates, String desc) {
        if (eventDates.isEmpty())
            throw new InvalidRequestException("Event Date must not be null");
        eventDates.forEach(localDate -> {
            if (localDate.isBefore(LocalDate.now()))
                throw new InvalidRequestException("Can not add a date in the past.");
        });
        Optional<Category> categoryOpt = categoryService.findCategoryById(Long.valueOf(categoryId));
        if (!categoryOpt.isPresent() || categoryOpt.get().isDeleted())
            throw new NoSuchElementException("category not found with id: " + categoryId);
        Tournament tournament = Tournament.builder().title(title).categoryId(categoryId)
                .description(desc)
                .format(TournamentFormat.ROUND_ROBIN)
                .status(TournamentStatus.NEED_INFORMATION)
                .matchDuration(0)
                .timeBetween(null)
                .build();
        tournament = tournamentRepository.save(tournament);
        final Integer tournamentId = tournament.getId();
        List<EventDate> events = null;
        if (eventDates != null) {
            events = eventDates.stream().map(date -> {
                return EventDate.builder().tournamentId(tournamentId)
                        .date(date)
                        .startTime(null)
                        .endTime(null)
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

    void editOrganizersInGeneral(TournamentUpdateDto tournamentUpdateDto, Integer tournamentId) {
        /*
        - Delete all organizers and save all organizers return from request
            even when organizer is not change
         */
        if (tournamentUpdateDto.getOrganizers() != null) {
            organizerTournamentService.deleteAllByTournamentId(tournamentId);
            organizerTournamentService.saveAll(tournamentUpdateDto.getOrganizers().stream().map(userId ->
                    OrganizerTournament.builder()
                            .userId(userService.getUserById(userId).getId())
                            .tournamentId(tournamentId)
                            .build()).toList());
        }
    }

    void editEventDatesInGeneral(TournamentUpdateDto tournamentUpdateDto, Integer tournamentId, Tournament tournament) {
        /*
          If status is NEED_INFORMATION
            + Delete all event dates and save all event dates return from request
          If status is READY, IN_PROGRESS
            + Add new event date and Delete all event dates in database that is not in request with conditions:
                - Event date is not today or before
                - Event date haven't match
         */
        if (tournamentUpdateDto.getEventDates() != null) {
            List<EventDate> eventDates = eventDateService.findAllByTournamentId(tournamentId);
            if (allowedResetAllEventDate.contains(tournament.getStatus())) {
                eventDateService.deleteAllByTournamentId(tournamentId);
            } else {
                for (EventDate eventDate : eventDates) {
                    LocalDate date = eventDate.getDate();
                    // If event date from database is not in request return -> delete it
                    if (!tournamentUpdateDto.getEventDates().contains(date)) {
                        if (!DateValidatorUtils.isAfterToday(date)) {
                            throw new InvalidRequestException("Cannot delete event date that is today or before");
                        } else if (matchService.isHaveMatchInDate(eventDate.getId())) {
                            throw new InvalidRequestException("Cannot delete event date that have match");
                        } else {
                            eventDateService.deleteByEventDateId(eventDate.getId());
                        }
                    } else {
                        // If event date from database is in request -> remove it from list
                        tournamentUpdateDto.setEventDates(tournamentUpdateDto.getEventDates().stream()
                                .filter(eventDate1 -> !eventDate1.equals(date)).toList());
                    }
                }
            }
            if (tournamentUpdateDto.getEventDates().stream().anyMatch(DateValidatorUtils::isBeforeToday)) {
                throw new InvalidRequestException("Cannot add event date that is before today");
            }

            eventDateService.saveAll(tournamentUpdateDto.getEventDates().stream().map(date -> EventDate.builder()
                    .tournamentId(tournamentId)
                    .date(date)
                    .startTime(LocalTime.MIN)
                    .endTime(LocalTime.of(23, 59, 59))
                    .build()).toList());
        }
    }

    void editTournamentInGeneral(TournamentUpdateDto tournamentUpdateDto, Tournament tournament) {
        tournament.setTitle((tournamentUpdateDto.getTitle() != null && allowedBasic.contains(tournament.getStatus())) ?
                tournamentUpdateDto.getTitle() :
                tournament.getTitle());

        tournament.setDescription((tournamentUpdateDto.getDescription() != null && allowedBasic.contains(tournament.getStatus())) ?
                tournamentUpdateDto.getDescription() :
                tournament.getDescription());

        //If not allow edit throw exception
        if (!allowedAdvance.contains(tournament.getStatus()) && tournamentUpdateDto.getCategoryId() != null) {
            throw new InvalidRequestException("Cannot update category of tournament");
        }

        tournament.setCategoryId(tournamentUpdateDto.getCategoryId() != null
                ? categoryService.findCategoryById(Long.valueOf(tournamentUpdateDto.getCategoryId()))
                        .orElseThrow(() -> new NoSuchElementException("Category not found")).getCategoryId().intValue()
                : tournament.getCategoryId());

        tournament.setUpdatedAt(LocalDateTime.now());

        tournamentRepository.save(tournament);
    }

    @Transactional
    public TournamentGeneralDto updateTournament(Integer tournamentId, TournamentUpdateDto tournamentUpdateDto) {
        //Check if data request is not valid throw exception
        TournamentUpdateDto.validateRequest(tournamentUpdateDto);

        // Find the tournament if not exist throw exception
        Tournament tournament = tournamentRepository.findTournamentByIdAndIsDeletedFalse(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        // Check if status is DELETED throw exception
        if (notAllowed.contains(tournament.getStatus())) {
            throw new InvalidRequestException("Cannot update tournament that is deleted");
        }

        /*
            * Edit event dates and organizers
                - If status is NEED_INFORMATION, READY, IN_PROGRESS
                    + Edit event dates and organizers
                    * If status is NEED_INFORMATION
                        + Reset all event dates
                - If status is DELETED, DISABLED, COMPLETED
                    -> Not Allow to edit event dates and organizers
         */

        if (allowedAdvance.contains(tournament.getStatus())) {
            editEventDatesInGeneral(tournamentUpdateDto, tournamentId, tournament);
            editOrganizersInGeneral(tournamentUpdateDto, tournamentId);
        } else if (!allowedAdvance.contains(tournament.getStatus()) && (tournamentUpdateDto.getEventDates() != null || tournamentUpdateDto.getOrganizers() != null)) {
            throw new InvalidRequestException("Cannot update tournament");
        }


        // Edit tournament general info
        editTournamentInGeneral(tournamentUpdateDto, tournament);

        return TournamentGeneralDto.builder()
                .id(tournament.getId())
                .title(tournament.getTitle())
                .description(tournament.getDescription())
                .category(categoryService.findCategoryDtoById(tournament.getCategoryId()))
                .status(tournament.getStatus())
                .eventDates(eventDateService.findAllByTournamentId(tournamentId))
                .organizers(userService.findOrganizerInGeneral(tournamentId))
                .build();
    }

    public Optional<Tournament> findById(Integer id) {
        return Optional.ofNullable(tournamentRepository.findTournamentByIdAndIsDeletedFalse(id).orElseThrow(() -> new NoSuchElementException("Tournament not found")));
    }

    public LeaderBoardDetailDto getDetailLeaderBoard(Integer tournamentId) {
        tournamentRepository.findTournamentByIdAndIsDeletedFalse(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        List<LeaderBoardDto> leaderBoard = matchService.getLeaderBoardByTournamentId(tournamentId);
        List<MatchOfLeaderBoardDto> matchOfLeaderBoardDto = matchService.getMatchOfLeaderBoardByTournamentId(tournamentId);

        return LeaderBoardDetailDto.builder()
                .leaderBoard(leaderBoard)
                .matches(matchOfLeaderBoardDto)
                .build();
    }

    public void discardTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findTournamentByIdAndIsDeletedFalse(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (tournament.getStatus() == TournamentStatus.DISCARDED) {
            throw new InvalidRequestException("Cannot discard tournament");
        }
        tournament.setStatus(TournamentStatus.DISCARDED);
        tournamentRepository.save(tournament);
    }

    public TournamentPlanDto getPlanByTournamentId(Integer tournamentId) {
        return tournamentRepository.getPlanByTournamentId(tournamentId).orElseThrow(() -> new NoSuchElementException("Tournament not found"));
    }

    public List<Tournament> findTournamentReadyNeedToChangeToInProgress() {
        return tournamentRepository.findTournamentReadyNeedToChangeToInProgress();
    }

    public List<Tournament> saveAll(List<Tournament> tournaments) {
        return tournamentRepository.saveAll(tournaments);
    }

    public List<Tournament> findTournamentInProgressNeedToChangeToFinished() {
        List<Object[]> result = tournamentRepository.findTournamentInProgressNeedToChangeToFinished();
        return result.stream().map(objects -> {
            Integer tournamentId = (Integer) objects[0];
            LocalTime endTime = ((Time) objects[1]).toLocalTime();
            LocalDate date = ((Date) objects[2]).toLocalDate();
            if (date.isBefore(LocalDate.now())
               || (date.isEqual(LocalDate.now()) && endTime.isBefore(LocalTime.now())))
                return findById(tournamentId).get();
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Tournament> findTournamentNeedInformationNeedToChangeToFinished() {
        List<Object[]> result = tournamentRepository.findTournamentNeedInformationNeedToChangeToFinished();
        return result.stream().map(objects -> {
            Integer tournamentId = (Integer) objects[0];
                return findById(tournamentId).get();
        }).collect(Collectors.toList());
    }

    public List<Tournament> findTournamentByCategoryId(Integer categoryId) {
        return tournamentRepository.findTournamentByCategoryId(categoryId);

    }
}
