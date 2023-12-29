package com.example.easytourneybe.match.dto;

import com.example.easytourneybe.match.Match;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {
    LocalDate date;
    List<Match> matches;
}
