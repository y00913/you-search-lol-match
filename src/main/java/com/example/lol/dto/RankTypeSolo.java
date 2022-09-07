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
public class RankTypeSolo {
    @Id
    private String id;
    private String queueType;
    private String userTier;
    private String userTierUrl;
    private String userRank;
    private int leaguePoints;
    private int userWins;
    private int userLosses;
}
