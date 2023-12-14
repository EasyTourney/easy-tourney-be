package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.eventdate.EventDateService;
import com.example.easytourneybe.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventDateService eventDateService;

    public List<TournamentDto> getAll(Integer page, Integer pageSize, String field, String sortType, TournamentStatus status, String search) {
        search = search.replaceAll("%", "\\\\%");
        search = search.replaceAll("_", "\\\\_");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false, isOrganizer = false;
        for (Object o : authentication.getAuthorities()) {
            if (UserRole.ADMIN.name().equals(o.toString())) isAdmin = true;
            if (UserRole.ORGANIZER.name().equals(o.toString())) isOrganizer = true;
        }
        List<TournamentDto> results;
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
}
