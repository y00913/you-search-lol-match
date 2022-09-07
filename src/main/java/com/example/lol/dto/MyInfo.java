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
    @ElementCollection
    private List<String> items;
    private String primaryPerk;
    private String subPerk;
    private int kills;
    private int deaths;
    private int assists;
    private String spell1Id;
    private String spell2Id;
}
