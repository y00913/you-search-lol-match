package com.example.lol.controller;

import com.example.lol.dto.SummonerDTO;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
