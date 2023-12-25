package com.example.easytourneybe.player;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.player.dto.Player;
import com.example.easytourneybe.player.interfaces.PlayerRepository;
import com.example.easytourneybe.team.interfaces.TeamRepository;
import com.example.easytourneybe.util.DateValidatorUtils;
import com.example.easytourneybe.validations.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;

    public void CheckTeamID(Long teamId) {
        List<Long> listTeamId= teamRepository.getAllTeamID();
        if (!listTeamId.contains(teamId)) {
            throw new NoSuchElementException("Team not found");
        }
    }
    public void CheckTeamHasPlayer(Long teamId, Long playerId) {
        Player player= playerRepository.findByPlayerIdAndTeamId(playerId, teamId);
        if (player==null) {
            throw new NoSuchElementException("Player may be not in this team");
        }
    }
    public List<Player> getAllPlayersByTeamID(Long teamId) {
        CheckTeamID(teamId);
        List<Player> listPlayer= playerRepository.getAllPlayersByTeamID(teamId);
        if (listPlayer.isEmpty()) {
            throw new NoSuchElementException("Player not found");
        }
        return listPlayer;
    }
    public long getTotalPlayers(Long teamId) {
        CheckTeamID(teamId);
        return playerRepository.getTotalPlayers(teamId);
    }
    public Player createPlayer(Long teamId, String playerName, String dob, String phoneNumber) {
        CheckTeamID(teamId);
        Player newPlayer = new Player();
        newPlayer.setTeamId(teamId);
        newPlayer.setPlayerName(playerName.trim());
        newPlayer.setDateOfBirth(dob);
        if (dob != null
                && !DateValidatorUtils.isBeforeToday(LocalDate.parse(dob.trim()))) {
            throw new InvalidRequestException("Date of birth must be before today");
        }
        newPlayer.setPhone(phoneNumber);
        newPlayer.setCreatedAt(LocalDateTime.now());
        return playerRepository.save(newPlayer);
    }

    public Player updatePlayer(Long teamId,Long playerId, String playerName, String dob, String phoneNumber) {
        CheckTeamID(teamId);
        Player existingPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));
        CheckTeamHasPlayer(teamId, playerId);
        existingPlayer.setPlayerName(playerName.trim());
        existingPlayer.setDateOfBirth(dob);
        if (dob != null
                && !DateValidatorUtils.isBeforeToday(LocalDate.parse(dob.trim()))) {
            throw new InvalidRequestException("Date of birth must be before today");
        }
        existingPlayer.setPhone(phoneNumber);
        existingPlayer.setUpdatedAt(LocalDateTime.now());

        return playerRepository.save(existingPlayer);
    }
    public Player deletePlayer(Long teamId,Long playerId) {
        CheckTeamID(teamId);
        Player existingPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));
        CheckTeamHasPlayer(teamId, playerId);
        playerRepository.delete(existingPlayer);
        return existingPlayer;
    }
    public Player getPlayerByPlayerID(Long teamId,Long playerId) {
        CheckTeamID(teamId);
        Player player= playerRepository.findByPlayerIdAndTeamId(playerId, teamId);
        if (player==null) {
            throw new NoSuchElementException("Player not found");
        }
        return player;
    }
}
