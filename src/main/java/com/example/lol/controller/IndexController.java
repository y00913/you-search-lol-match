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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    public String getResult(String summonerName, Model model){
        String summonerNameRepl = summonerName.replaceAll(" ","%20");
        SummonerDTO summonerDTO = summonerService.callRiotAPISummonerByName(summonerNameRepl);

        model.addAttribute("result",summonerDTO);

        if(summonerDTO != null) {
            String iconUrl = iconService.callSummonerIcon(summonerDTO.getProfileIconId());
            model.addAttribute("iconUrl", iconUrl);

            List<LeagueEntryDTO> leagueEntry = summonerService.callLeagueEntry(summonerDTO.getId());
            model.addAttribute("leagueEntry", leagueEntry);

            if (leagueEntry.size() != 0) {
                model.addAttribute("tierUrl", iconService.callTierIcon(leagueEntry.get(0).getTier()));
            }
        }

        return "result";
    }

    @GetMapping("/{summonerName}/{start}")
    public String callMatchTable(@PathVariable String summonerName, @PathVariable int start, Model model) {
        String summonerNameRepl = summonerName.replaceAll(" ","%20");
        SummonerDTO summonerDTO = summonerService.callRiotAPISummonerByName(summonerNameRepl);

        model.addAttribute("result",summonerDTO);

        if(summonerDTO != null) {
            List<String> matchHistory = summonerService.callMatchHistory(summonerDTO.getPuuid(), start  );

            System.out.println(summonerDTO);
            System.out.println(matchHistory);

            List<MatchDTO> matchDTOs = summonerService.callMatchAbout(matchHistory,summonerName);

            model.addAttribute("matches",matchDTOs);

            System.out.println(matchDTOs.get(0).toString());
        }

        return "table";
    }

    @GetMapping("/detail/{matchId}")
    public String callDeatilMatchTable(@PathVariable String matchId, Model model) {
        MatchDTO matchDTO = summonerService.callDetailMatch(matchId);

        model.addAttribute("match",matchDTO);

        System.out.println(matchId);

        return "detail-table";
    }

}
