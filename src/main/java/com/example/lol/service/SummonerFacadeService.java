package com.example.lol.service;

import com.example.lol.dto.RiotInfo;
import com.example.lol.dto.Summoner;
import com.example.lol.util.SummonerNameParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

}
