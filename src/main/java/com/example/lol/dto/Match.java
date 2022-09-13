package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String puuid;
    private String matchId;
    private String queueType;
    private Long gameDurationMinutes;
    private Long gameDurationSeconds;
    private boolean win;
    private Long endTimeStamp;
    @Transient
    private String endTime;
//    @OneToOne
//    @JoinColumn(name = "my_info_id")
//    private MyInfo myInfoDTO;
    private String championName;
    private String primaryPerk;
    private String subPerk;
    private int kills;
    private int deaths;
    private int assists;
    private String spell1Id;
    private String spell2Id;
    private String item0;
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String item5;
    private String item6;
}
