package com.example.lol.controller;

import com.example.lol.dto.LeagueEntryDTO;
import com.example.lol.dto.MatchDTO;
import com.example.lol.dto.SummonerDTO;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private SummonerService summonerService;

    @GetMapping
    public String index() {

        return "index";
    }

    @GetMapping("/search")
    public String getResult(String summonerName, Model model) {
        String summonerNameRepl = summonerName.replaceAll(" ","%20");
        SummonerDTO summonerDTO = summonerService.callRiotAPISummonerByName(summonerNameRepl);

        if (summonerDTO.getName() != null) {
            model.addAttribute("summoner", summonerDTO);
        }

        System.out.println(summonerDTO);

        return "result";
    }

    @GetMapping("/{id}/{name}/{summonerLevel}/{profileIcon}")
    public String callLeagueInfo(@PathVariable String id, @PathVariable String name, @PathVariable String summonerLevel, @PathVariable String profileIcon, Model model){
        LeagueEntryDTO leagueEntry = summonerService.callLeagueEntry(id);
        model.addAttribute("leagueEntry", leagueEntry);
        model.addAttribute("name", name);
        model.addAttribute("summonerLevel", summonerLevel);
        model.addAttribute("profileIcon", "https://ddragon.leagueoflegends.com/cdn/12.15.1/img/profileicon/" + profileIcon + ".png");

        return "league-info";
    }

    @GetMapping("/{puuid}/{start}")
    public String callMatchTable(@PathVariable String puuid, @PathVariable int start, Model model) {
        List<String> matchHistory = summonerService.callMatchHistory(puuid, start);
        List<MatchDTO> matchDTOs = summonerService.callMatchAbout(matchHistory, puuid);

        model.addAttribute("matches", matchDTOs);

        return "table :: body";
    }

    @GetMapping("/detail/{matchId}")
    public String callDeatilMatchTable(@PathVariable String matchId, Model model) {
        MatchDTO matchDTO = summonerService.callDetailMatch(matchId);

        model.addAttribute("match", matchDTO);

        return "detail-table :: body";
    }

}
