package com.example.lol.controller;

import com.example.lol.dto.MatchDTO;
import com.example.lol.dto.SummonerDTO;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SummonerApiController {

    @Autowired
    private SummonerService summonerService;

    @PostMapping("/summonerByName/{summonerName}")
    public SummonerDTO callSummonerByName(@PathVariable String summonerName){

        summonerName = summonerName.replaceAll(" ","%20");

        SummonerDTO result = summonerService.callRiotAPISummonerByName(summonerName);

        return result;
    }

    @PostMapping("/match")
    public List<MatchDTO> callMatchDTO(@RequestParam String summonerName, @RequestParam int start) {
        List<MatchDTO> matchDTOs = new ArrayList<>();
        String summonerNameRepl = summonerName.replaceAll(" ","%20");
        SummonerDTO summonerDTO = summonerService.callRiotAPISummonerByName(summonerNameRepl);

        List<String> matchHistory = summonerService.callMatchHistory(summonerDTO.getPuuid(), start);
        for(String match : matchHistory) {
            MatchDTO matchDTO = summonerService.callMatchAbout(match, summonerName);
            matchDTOs.add(matchDTO);
        }

        System.out.println(summonerName);

        return matchDTOs;
    }

}
