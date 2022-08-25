package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeagueEntryDTO {
    private QueueTypeDTO Ranked_Flex;
    private QueueTypeDTO Ranked_Solo;
}
