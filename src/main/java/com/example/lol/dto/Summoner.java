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
public class Summoner {
    @Id
    private String puuid;
    private String name;
    private String id;
    private String profileIcon;
    private Long summonerLevel;
}
