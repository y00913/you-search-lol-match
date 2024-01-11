package com.example.lol.controller;

import com.example.lol.dto.RiotInfo;
import com.example.lol.dto.StartTimeMapping;
import com.example.lol.dto.Summoner;
import com.example.lol.repository.*;
import com.example.lol.service.SummonerService;
import com.example.lol.util.SummonerNameParse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SummonerApiController {

    @Autowired
    private SummonerService summonerService;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/renewal/{name}/{tagLine}")
    public void  renewal(@PathVariable String name, @PathVariable String tagLine){
        RiotInfo riotInfo = new RiotInfo(name, tagLine);
        Summoner summoner = summonerService.callRiotAPISummonerByPuuid(riotInfo);
        summonerService.callRankTier(summoner.getId());
        Optional<StartTimeMapping> startTime = matchRepository.findTop1ByPuuid(summoner.getPuuid(), Sort.by(Sort.Direction.DESC, "endTimeStamp"));
        List<String> matchHistory = new ArrayList<>();
        if(startTime.isPresent()) {
            matchHistory = summonerService.callMatchHistory(summoner.getPuuid(), startTime.get().getEndTimeStamp() + 10);
        } else {
            matchHistory = summonerService.callMatchHistory(summoner.getPuuid(), 0L);
        }
        summonerService.callMatchAbout(matchHistory, summoner.getPuuid());
    }

}
