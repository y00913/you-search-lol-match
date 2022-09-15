package com.example.lol.controller;

import com.example.lol.dto.*;
import com.example.lol.repository.*;
import com.example.lol.service.IconService;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private MatchRepository matchRepository;

    @GetMapping
    public String index() {

        return "index";
    }

    @GetMapping("/renewal/{summonerName}")
    public String renewal(@PathVariable String summonerName){
        Summoner summoner = summonerService.callRiotAPISummonerByName(summonerName, true);
        summonerService.callRankTier(summoner.getId());
        Optional<StartTimeMapping> startTime = matchRepository.findTop1ByPuuid(summoner.getPuuid(), Sort.by(Sort.Direction.DESC, "endTimeStamp"));
        List<String> matchHistory = new ArrayList<>();
        if(startTime.isPresent()) {
            matchHistory = summonerService.callMatchHistory(summoner.getPuuid(), startTime.get().getEndTimeStamp() + 10);
        } else {
            matchHistory = summonerService.callMatchHistory(summoner.getPuuid(), 0L);
        }
        summonerService.callMatchAbout(matchHistory, summoner.getPuuid());

        return "result";
    }

    @GetMapping("/search")
    public String getResult(String summonerName, Model model) {
        Optional<Summoner> summonerDTO = summonerRepository.findByName(summonerName);

        if (summonerDTO.isPresent()) {
            model.addAttribute("summoner", summonerDTO.get());
            model.addAttribute("check", true);
        } else {
            Summoner tmp = summonerService.callRiotAPISummonerByName(summonerName, false);
            if(tmp.getName() != null){
                model.addAttribute("summoner", tmp);
                model.addAttribute("check", false);
            }
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
//        List<String> matchHistory = summonerService.callMatchHistory(puuid, start);
//        List<Match> matchDTOs = summonerService.callMatchAbout(matchHistory, puuid);
        Page<Match> matchDTOs = matchRepository.findByPuuid(PageRequest.of(start,10, Sort.Direction.DESC, "endTimeStamp"), puuid);
        for(Match match : matchDTOs){
            Match tmp = match;

            tmp.setItem0(iconService.callItemIcon(tmp.getItem0()));
            tmp.setItem1(iconService.callItemIcon(tmp.getItem1()));
            tmp.setItem2(iconService.callItemIcon(tmp.getItem2()));
            tmp.setItem3(iconService.callItemIcon(tmp.getItem3()));
            tmp.setItem4(iconService.callItemIcon(tmp.getItem4()));
            tmp.setItem5(iconService.callItemIcon(tmp.getItem5()));
            tmp.setItem6(iconService.callItemIcon(tmp.getItem6()));
            tmp.setChampionName(iconService.callChampionIcon(tmp.getChampionName()));
            tmp.setSpell1Id(iconService.callSpellIcon(tmp.getSpell1Id()));
            tmp.setSpell2Id(iconService.callSpellIcon(tmp.getSpell2Id()));
            tmp.setPrimaryPerk(iconService.callPrimaryPerkIcon(tmp.getPrimaryPerk()));
            tmp.setSubPerk(iconService.callSubPerkIcon(tmp.getSubPerk()));

            long timestamp = System.currentTimeMillis() / 1000 - tmp.getEndTimeStamp();
            String endTime;
            if (timestamp < 60) {
                endTime = Math.round(timestamp) + "초 전";
            } else if (timestamp / 60 < 60) {
                endTime = Math.round(timestamp / 60) + "분 전";
            } else if (timestamp / 60 / 60 < 24) {
                endTime = Math.round(timestamp / 60 / 60) + "시간 전";
            } else if (timestamp / 60 / 60 / 24 < 30) {
                endTime = Math.round(timestamp / 60 / 60 / 24) + "일 전";
            } else {
                endTime = Math.round(timestamp / 60 / 60 / 24 / 30) + "달 전";
            }
            tmp.setEndTime(endTime);
        }

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
