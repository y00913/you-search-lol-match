package com.example.lol.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IconService {
    private String version = "14.19.1";

    public String callProfileIcon(String profileIcon){
        String url = "https://ddragon.leagueoflegends.com/cdn/" + version + "/img/profileicon/" + profileIcon + ".png";

        return url;
    }

    public String callTierIcon(String tier) {
        if(tier == null) {
            tier = "unranked";
        }

        String url ="/static/img/" + tier.toLowerCase() + ".png";

        return url;
    }

    public String callItemIcon(String item) {
        String url = "https://ddragon.leagueoflegends.com/cdn/" + version + "/img/item/" + item + ".png";

        if (item.equals("0")) {
            url = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Regular_quadrilateral.svg/220px-Regular_quadrilateral.svg.png";
        }

        return url;
    }

    public String callChampionIcon(String champion) {
        String url = "https://ddragon.leagueoflegends.com/cdn/" + version + "/img/champion/" + champion + ".png";

        if (champion.equals("FiddleSticks")) {
            url = "https://ddragon.leagueoflegends.com/cdn/" + version + "/img/champion/" + "Fiddlesticks" + ".png";
        }

        return url;
    }

    public String callPrimaryPerkIcon(String perk) {
        String url = "https://opgg-static.akamaized.net/images/lol/perk/" + perk + ".png?image=q_auto,f_webp,w_auto&v=1660789334950";

        if(perk.equals("0")) {
            url = "";
        }

        return url;
    }

    public String callSubPerkIcon(String perk) {
        String url = "https://opgg-static.akamaized.net/images/lol/perkStyle/" + perk + ".png?image=q_auto,f_webp,w_auto&v=1660789334950";

        if(perk.equals("0")) {
            url = "";
        }

        return url;
    }

    String spellUrl;

    public void findUrl(String url) {
        spellUrl = url;
    }

    public String callSpellIcon(String spellId) {
        String url = "https://ddragon.leagueoflegends.com/cdn/" + version + "/data/en_US/summoner.json";

        try {
            WebClient webClient = WebClient.builder().baseUrl(url).build();
            List<String> body = webClient.get()
                    .retrieve()
                    .bodyToFlux(String.class)
                    .toStream()
                    .collect(Collectors.toList());

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(body.get(0));
            JSONObject data = (JSONObject) jsonObject.get("data");

            data.keySet().forEach(key -> {
                        JSONObject spellInfo = (JSONObject) data.get(key);

                        if (spellInfo.get("key").equals(spellId)) {
                            JSONObject image = (JSONObject) spellInfo.get("image");
                            String spell = "https://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/" + image.get("full").toString();
                            findUrl(spell);
                        }
                    }
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return spellUrl;
    }
}
