package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;

import java.util.List;

public interface TournamentRepositoryCustom {
    List<TournamentDto> findAllByUserId(Integer userId, Integer page, Integer pageSize, String sortType, String field, TournamentStatus status, String search);
}
