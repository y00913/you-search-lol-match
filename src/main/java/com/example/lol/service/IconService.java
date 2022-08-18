package com.example.lol.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:riotApiKey.properties")
public class IconService {
    @Value("${riotApiKey}")
    private String myKey;

    public String callSummonerIcon(int profileIconId) {
        String url = "https://ddragon.leagueoflegends.com/cdn/12.15.1/img/profileicon/" + profileIconId + ".png";

        return url;
    }

    public String callTierIcon(String tier) {
        String url = "https://opgg-static.akamaized.net/images/medals_new/" + tier + ".png?image=q_auto,f_webp,w_144&v=1660126953027";

        return url;
    }
}
