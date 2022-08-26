package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeagueEntryDTO {
    private RankTypeDTO Ranked_Flex;
    private RankTypeDTO Ranked_Solo;
}
