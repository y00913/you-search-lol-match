package com.example.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyInfo {
    @Id
    @Column(name ="my_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myInfoId;
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
