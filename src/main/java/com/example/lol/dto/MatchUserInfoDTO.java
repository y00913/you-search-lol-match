package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchUserInfoDTO {
    private String summonerName;
    private boolean win;
    private String championName;
    private int champLevel;
    private int kills;
    private int deaths;
    private int assists;
    private int totalMinionsKilled;
    private int totalDamageDealtToChampions;
    private List<String> items;
    private String primaryPerk;
    private String subPerk;
    private String spell1Id;
    private String spell2Id;
}
