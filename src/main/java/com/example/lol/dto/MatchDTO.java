package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private String matchId;
    private String queueType;
    private List<MatchUserInfoDTO> matchUserInfoDTOs;
    private Long gameDurationMinutes;
    private Long gameDurationSeconds;
    private boolean win;
    private String endTime;
    private MyInfoDTO myInfoDTO;
}
