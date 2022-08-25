package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummonerDTO {
    private String name;
    private String id;
    private String puuid;
    private String profileIcon;
    private long summonerLevel;
}
