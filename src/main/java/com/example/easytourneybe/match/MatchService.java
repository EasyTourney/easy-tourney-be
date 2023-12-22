package com.example.easytourneybe.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
    @Autowired
    MatchRepository matchRepository;

    public boolean isHaveMatchInDate(Integer eventDateId) {
        return matchRepository.isHaveMatchInDate(eventDateId);
    }
}
