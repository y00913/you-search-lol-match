package com.example.lol.controller;

import com.example.lol.dto.Match;
import com.example.lol.dto.Summoner;
import com.example.lol.repository.*;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SummonerApiController {

    @Autowired
    private SummonerService summonerService;

    @Autowired
    private SummonerRepository summonerRepository;

    @Autowired
    private RankTypeFlexRepository rankTypeFlexRepository;

    @Autowired
    private RankTypeSoloRepository rankTypeSoloRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MyInfoRepository myInfoRepository;

    @PostMapping("/summonerByName/{summonerName}")
    public Summoner callSummonerByName(@PathVariable String summonerName){
        Summoner result = summonerRepository.findByName(summonerName);

        return result;
    }

    @GetMapping("/match/{summonerName}/{start}")
    public List<Match> callMatchDTO(@PathVariable String summonerName, @PathVariable int start) {
        Summoner summonerDTO = summonerRepository.findByName(summonerName);

        List<String> matchHistory = summonerService.callMatchHistory(summonerDTO.getPuuid(), start);
        List<Match> matchDTOs = summonerService.callMatchAbout(matchHistory,summonerName);

        return matchDTOs;
    }

}
