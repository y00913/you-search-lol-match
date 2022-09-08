package com.example.lol.controller;

import com.example.lol.dto.LeagueEntry;
import com.example.lol.dto.Match;
import com.example.lol.dto.MatchUserInfo;
import com.example.lol.dto.Summoner;
import com.example.lol.repository.*;
import com.example.lol.service.IconService;
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

    @Autowired
    private IconService iconService;

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

    @GetMapping
    public String index() {

        return "index";
    }

    @GetMapping("/search")
    public String getResult(String summonerName, Model model) {
        Summoner summonerDTO = summonerRepository.findByName(summonerName);

        if (summonerDTO.getName() != null) {
            model.addAttribute("summoner", summonerDTO);
        }

        System.out.println(summonerDTO);

        return "result";
    }

    @GetMapping("/{id}/{name}/{summonerLevel}/{profileIcon}")
    public String callLeagueInfo(@PathVariable String id, @PathVariable String name, @PathVariable String summonerLevel, @PathVariable String profileIcon, Model model){
        LeagueEntry leagueEntry = summonerService.callLeagueEntry(id);
        model.addAttribute("leagueEntry", leagueEntry);
        model.addAttribute("name", name);
        model.addAttribute("summonerLevel", summonerLevel);
        model.addAttribute("profileIcon", iconService.callProfileIcon(profileIcon));

        return "league-info";
    }

    @GetMapping("/{puuid}/{start}")
    public String callMatchTable(@PathVariable String puuid, @PathVariable int start, Model model) {
        List<String> matchHistory = summonerService.callMatchHistory(puuid, start);
        List<Match> matchDTOs = summonerService.callMatchAbout(matchHistory, puuid);

        model.addAttribute("matches", matchDTOs);

        return "table :: body";
    }

    @GetMapping("/detail/{matchId}")
    public String callDeatilMatchTable(@PathVariable String matchId, Model model) {
        List<MatchUserInfo> matchUserInfoDTOs = summonerService.callDetailMatch(matchId);

        model.addAttribute("matchInfo", matchUserInfoDTOs);

        return "detail-table :: body";
    }

}
