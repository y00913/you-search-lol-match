package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchUserInfoDTO {
    private String summonerName;
    private boolean win;
    private String championName;
    private int kills;
    private int deaths;
    private int assists;
    private int totalMinionsKilled;
    private int totalDamageDealtToChampions;
    private int item0;
    private int item1;
    private int item2;
    private int item3;
    private int item4;
    private int item5;
    private int item6;
    private int primaryPerk;
    private int subPerk;
}
