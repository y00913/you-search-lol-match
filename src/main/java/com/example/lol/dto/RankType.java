package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankType {
    @Id
    private String id;
    private String flexUserTier;
    private String flexUserRank;
    private int flexLeaguePoints;
    private int flexUserWins;
    private int flexUserLosses;
    private String soloUserTier;
    private String soloUserRank;
    private int soloLeaguePoints;
    private int soloUserWins;
    private int soloUserLosses;
}
