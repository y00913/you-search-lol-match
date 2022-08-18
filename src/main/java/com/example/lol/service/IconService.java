package com.example.lol.service;

import org.springframework.stereotype.Service;

@Service
public class IconService {
    public String callSummonerIcon(int profileIconId) {
        String url = "https://ddragon.leagueoflegends.com/cdn/12.15.1/img/profileicon/" + profileIconId + ".png";

        return url;
    }

    public String callTierIcon(String tier) {
        String url = "https://opgg-static.akamaized.net/images/medals_new/" + tier + ".png?image=q_auto,f_webp,w_144&v=1660126953027";

        return url;
    }

    public String callItemIcon(String item){
        String url = "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/item/" + item + ".png";

        if(item.equals("0")){
            url = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Regular_quadrilateral.svg/220px-Regular_quadrilateral.svg.png";
        }

        return url;
    }

    public String callChampionIcon(String champion){
        String url = "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/champion/" + champion + ".png";

        return url;
    }

    public String callPrimaryPerkIcon(String perk){
        String url = "https://opgg-static.akamaized.net/images/lol/perk/" + perk + ".png?image=q_auto,f_webp,w_auto&v=1660789334950";

        return url;
    }

    public String callSubPerkIcon(String perk){
        String url = "https://opgg-static.akamaized.net/images/lol/perkStyle/" + perk + ".png?image=q_auto,f_webp,w_auto&v=1660789334950";

        return url;
    }
}
