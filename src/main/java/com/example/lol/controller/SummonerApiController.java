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

    @GetMapping("/match/{summonerName}/{start}")
    public List<MatchDTO> callMatchDTO(@PathVariable String summonerName, @PathVariable int start) {
        String summonerNameRepl = summonerName.replaceAll(" ","%20");
        SummonerDTO summonerDTO = summonerService.callRiotAPISummonerByName(summonerNameRepl);

        List<String> matchHistory = summonerService.callMatchHistory(summonerDTO.getPuuid(), start);
        List<MatchDTO> matchDTOs = summonerService.callMatchAbout(matchHistory,summonerName);

        return matchDTOs;
    }

}
