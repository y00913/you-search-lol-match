package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RankTypeDto {

    private RankType rankType;
    private String flexUserTier;
    private String soloUserTier;

}
