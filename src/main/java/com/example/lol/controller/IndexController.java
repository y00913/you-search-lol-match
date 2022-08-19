package com.example.lol.controller;

import com.example.lol.dto.LeagueEntryDTO;
import com.example.lol.dto.MatchDTO;
import com.example.lol.dto.MatchUserInfoDTO;
import com.example.lol.dto.SummonerDTO;
import com.example.lol.service.IconService;
import com.example.lol.service.SummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private SummonerService summonerService;

    @Autowired
    private IconService iconService;

    @GetMapping
    public String index(){

        return "index";
    }

    @PostMapping
    public String postResult(String summonerName, Model model){
        String summonerNameRepl = summonerName.replaceAll(" ","%20");

        SummonerDTO result = summonerService.callRiotAPISummonerByName(summonerNameRepl);
        model.addAttribute("result",result);

        if(result != null) {
            String iconUrl = iconService.callSummonerIcon(result.getProfileIconId());
            model.addAttribute("iconUrl",iconUrl);

            List<LeagueEntryDTO> leagueEntry = summonerService.callLeagueEntry(result.getId());
            model.addAttribute("leagueEntry",leagueEntry);

            if(leagueEntry.size() != 0) {
                model.addAttribute("tierUrl", iconService.callTierIcon(leagueEntry.get(0).getTier()));
            }

            List<String> matchHistory = summonerService.callMatchHistory(result.getPuuid(), 0);

            System.out.println(result);
            System.out.println(matchHistory);

            List<MatchDTO> matchDTOs = new ArrayList<>();

            for(String match : matchHistory) {
                MatchDTO matchDTO = summonerService.callMatchAbout(match, summonerName);
                matchDTOs.add(matchDTO);
            }

            model.addAttribute("matches",matchDTOs);
        }

        return "result";
    }

}
