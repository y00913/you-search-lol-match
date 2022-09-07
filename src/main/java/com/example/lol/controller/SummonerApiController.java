package com.example.lol.controller;

import com.example.lol.dto.Match;
import com.example.lol.dto.Summoner;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SummonerApiController {

    @Autowired
    private SummonerService summonerService;

    @PostMapping("/summonerByName/{summonerName}")
    public Summoner callSummonerByName(@PathVariable String summonerName){

        summonerName = summonerName.replaceAll(" ","%20");

        Summoner result = summonerService.callRiotAPISummonerByName(summonerName);

        return result;
    }

    @GetMapping("/match/{summonerName}/{start}")
    public List<Match> callMatchDTO(@PathVariable String summonerName, @PathVariable int start) {
        String summonerNameRepl = summonerName.replaceAll(" ","%20");
        Summoner summonerDTO = summonerService.callRiotAPISummonerByName(summonerNameRepl);

        List<String> matchHistory = summonerService.callMatchHistory(summonerDTO.getPuuid(), start);
        List<Match> matchDTOs = summonerService.callMatchAbout(matchHistory,summonerName);

        return matchDTOs;
    }

}
