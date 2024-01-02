package com.example.easytourneybe.util;

import com.example.easytourneybe.enums.tournament.TournamentStatus;

import java.util.Arrays;
import java.util.List;

public class TournamentStatusPermission {
    public static final List<TournamentStatus> notAllowed = List.of(TournamentStatus.DELETED);

    public static final List<TournamentStatus> allowedBasic = Arrays.stream(TournamentStatus.values()).filter(status -> !notAllowed.contains(status)).toList();

    public static final List<TournamentStatus> allowedAdvance = List.of(TournamentStatus.NEED_INFORMATION, TournamentStatus.READY, TournamentStatus.IN_PROGRESS);

    public static final List<TournamentStatus> allowedResetAllEventDate = List.of(TournamentStatus.NEED_INFORMATION);
}
