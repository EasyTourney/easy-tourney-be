package com.example.easytourneybe.match.dto;

import com.example.easytourneybe.match.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ResponseChangeMatch {
    Map<Integer, List<Match>> data;
}
