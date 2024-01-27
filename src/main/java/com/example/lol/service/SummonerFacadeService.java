package com.example.lol.service;

import com.example.lol.dto.*;
import com.example.lol.util.SummonerNameParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummonerFacadeService {

    private final SummonerService summonerService;
    private final IconService iconService;

    public Summoner getSummoner(String nameAndTag){
        RiotInfo riotInfo = SummonerNameParse.getNameAndTage(nameAndTag);
        Optional<Summoner> summonerDTO = summonerService.findSummoner(riotInfo);

        if(summonerDTO.isEmpty()) {
            summonerDTO = Optional.of(summonerService.callRiotAPISummonerByPuuid(riotInfo));
        }

        log.info(summonerDTO.toString());

        return summonerDTO.get();
    }

    public Boolean checkSummonerMatch(String puuid){
        if(puuid == null) return false;
        return summonerService.checkMatch(puuid);
    }

    public RankTypeDto getSummonerRankInfo(String id){
        RankType rankType = summonerService.findRankType(id).orElse(summonerService.callRankTier(id));

        return RankTypeDto.builder()
                .rankType(rankType)
                .flexUserTier(iconService.callTierIcon(rankType.getFlexUserTier()))
                .soloUserTier(iconService.callTierIcon(rankType.getSoloUserTier()))
                .build();
    }

    public String getProfileIcon(String profileIcon) {
        return iconService.callProfileIcon(profileIcon);
    }

    public Page<Match> getPagingMatch(String puuid, int start) {
        Page<Match> matchDTOs = summonerService.findPagingMatch(puuid, start);
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

        return matchDTOs;
    }

    public List<MatchUserInfo> getDetailMatch(String matchId){
        List<MatchUserInfo> matchUserInfoList = summonerService.callDetailMatch(matchId);

        for(MatchUserInfo matchUserInfo : matchUserInfoList) {
            matchUserInfo.setChampionName(iconService.callChampionIcon(matchUserInfo.getChampionName()));

            List<String> items = new ArrayList<>();
            for(int i = 0; i<= 6; i++) {
                items.add(iconService.callItemIcon(matchUserInfo.getItems().get(i)));
            }
            matchUserInfo.setItems(items);

            matchUserInfo.setPrimaryPerk(iconService.callPrimaryPerkIcon(matchUserInfo.getPrimaryPerk()));
            matchUserInfo.setSubPerk(iconService.callSubPerkIcon(matchUserInfo.getSubPerk()));
            matchUserInfo.setSpell1Id(iconService.callSpellIcon(matchUserInfo.getSpell1Id()));
            matchUserInfo.setSpell2Id(iconService.callSpellIcon(matchUserInfo.getSpell2Id()));
        }

        return matchUserInfoList;
    }

    public void callRenewal(String name, String tagLine) {
        RiotInfo riotInfo = new RiotInfo(name, tagLine);
        Summoner summoner = summonerService.callRiotAPISummonerByPuuid(riotInfo);
        summonerService.callRankTier(summoner.getId());
        Optional<StartTimeMapping> startTime = summonerService.getStartTime(summoner.getPuuid());
        List<String> matchHistory = new ArrayList<>();
        if(startTime.isPresent()) {
            matchHistory = summonerService.callMatchHistory(summoner.getPuuid(), startTime.get().getEndTimeStamp() + 10);
        } else {
            matchHistory = summonerService.callMatchHistory(summoner.getPuuid(), 0L);
        }
        summonerService.callMatchAbout(matchHistory, summoner.getPuuid());
    }

}
