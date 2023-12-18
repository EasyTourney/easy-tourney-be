package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.user.UserService;
import com.example.easytourneybe.user.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public ResponseObject getAll(Integer page, Integer pageSize, String field, String sortType, TournamentStatus status, String search) {
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
            results = tournamentRepository.findAllByUserId(null, page, pageSize, sortType, field, status, search);
       } else if (isOrganizer) {
            Integer userId = userService.findByEmail(authentication.getName()).getId();
            results = tournamentRepository.findAllByUserId(userId, page, pageSize, sortType, field, status, search);
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
}
