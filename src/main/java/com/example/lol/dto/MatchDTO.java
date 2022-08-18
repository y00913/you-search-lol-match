package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private List<com.example.lol.dto.MatchUserInfoDTO> matchUserInfoDTOs;
    private Long gameDurationMinutes;
    private Long gameDurationSeconds;
    private String gameMode;
    private boolean win;
}
