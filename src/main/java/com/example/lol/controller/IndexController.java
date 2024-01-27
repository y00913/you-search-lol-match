package com.example.lol.controller;

import com.example.lol.dto.*;
import com.example.lol.service.SummonerFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

    private final SummonerFacadeService summonerFacadeService;

    @GetMapping
    public String index() {

        return "index";
    }

    @GetMapping("/search")
    public String getResult(String nameAndTag, Model model) {
        Summoner summonerDTO = summonerFacadeService.getSummoner(nameAndTag);

        if(summonerDTO.getPuuid() != null) {
            model.addAttribute("summoner", summonerDTO);
        }

        Boolean check = summonerFacadeService.checkSummonerMatch(summonerDTO.getPuuid());
        model.addAttribute("check", check);

        return "result";
    }

    @GetMapping("/{id}/{name}/{tagLine}/{summonerLevel}/{profileIcon}/{updateAt}")
    public String callLeagueInfo(@PathVariable String id,
                                 @PathVariable String name,
                                 @PathVariable String tagLine,
                                 @PathVariable String summonerLevel,
                                 @PathVariable String profileIcon,
                                 @PathVariable String updateAt,
                                 Model model){
        RankTypeDto rankTypeDto = summonerFacadeService.getSummonerRankInfo(id);

        model.addAttribute("rankType", rankTypeDto.getRankType());
        model.addAttribute("flexTierImg", rankTypeDto.getFlexUserTier());
        model.addAttribute("soloTierImg", rankTypeDto.getSoloUserTier());

        model.addAttribute("name", name);
        model.addAttribute("tagLine", tagLine);
        model.addAttribute("summonerLevel", summonerLevel);
        model.addAttribute("profileIcon", summonerFacadeService.getProfileIcon(profileIcon));

        Long diff = ChronoUnit.MINUTES.between(LocalDateTime.parse(updateAt), LocalDateTime.now());
        model.addAttribute("updateAt", diff);

        return "league-info";
    }

    @GetMapping("/{puuid}/{start}")
    public String callMatchTable(@PathVariable String puuid, @PathVariable int start, Model model) {
        Page<Match> matchDTOs = summonerFacadeService.getPagingMatch(puuid, start);

        model.addAttribute("matches", matchDTOs.getContent());

        return "table :: body";
    }

    @GetMapping("/detail/{matchId}")
    public String callDeatilMatchTable(@PathVariable String matchId, Model model) {
        List<MatchUserInfo> matchUserInfoDTOs = summonerFacadeService.getDetailMatch(matchId);

        model.addAttribute("matchInfo", matchUserInfoDTOs);

        return "detail-table :: body";
    }

    @GetMapping("/riot.txt")
    public String callRiotText(Model model){
        return "riot.txt";
    }
}
