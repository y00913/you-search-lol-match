package com.example.lol.controller;

import com.example.lol.service.SummonerFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SummonerApiController {

    private final SummonerFacadeService summonerFacadeService;

    @GetMapping("/renewal/{name}/{tagLine}")
    public void renewal(@PathVariable String name, @PathVariable String tagLine){
        summonerFacadeService.callRenewal(name, tagLine);
    }

}
