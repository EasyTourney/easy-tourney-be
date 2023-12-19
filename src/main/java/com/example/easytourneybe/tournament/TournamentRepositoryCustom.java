package com.example.easytourneybe.tournament;

import com.example.easytourneybe.enums.tournament.TournamentStatus;
import com.example.easytourneybe.model.ResponseObject;

import java.util.List;

public interface TournamentRepositoryCustom {
    ResponseObject findAllByUserId(Integer userId, Integer page, Integer pageSize, String sortType, String field, TournamentStatus status, String search, Integer categoryId);
}
