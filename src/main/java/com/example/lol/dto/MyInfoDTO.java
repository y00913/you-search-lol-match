package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyInfoDTO {
    private String championName;
    private List<String> items;
    private String primaryPerk;
    private String subPerk;
    private int kills;
    private int deaths;
    private int assists;
    private String spell1Id;
    private String spell2Id;
}
