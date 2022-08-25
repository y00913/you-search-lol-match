package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueTypeDTO {
    private String queueType;
    private String tier;
    private String tierUrl;
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;
}
