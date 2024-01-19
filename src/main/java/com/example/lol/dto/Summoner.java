package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summoner {
    @Id
    private String puuid;
    private String name;
    private String tagLine;
    private String id;
    private String profileIcon;
    private Long summonerLevel;
    private LocalDateTime updateAt;
}
