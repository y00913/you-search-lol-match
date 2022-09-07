package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeagueEntry {
    private RankTypeFlex Ranked_Flex;
    private RankTypeSolo Ranked_Solo;
}
