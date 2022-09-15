package com.example.lol.controller;

import com.example.lol.dto.Summoner;
import com.example.lol.repository.*;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SummonerApiController {

    @Autowired
    private SummonerService summonerService;

    @Autowired
    private SummonerRepository summonerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @PostMapping("/summonerByName/{summonerName}")
    public Summoner callSummonerByName(@PathVariable String summonerName){
        Optional<Summoner> result = summonerRepository.findByName(summonerName);

        return result.get();
    }

}
