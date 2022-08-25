package com.example.lol.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IconService {
    public String callSummonerIcon(String profileIconId) {
        String url = "https://ddragon.leagueoflegends.com/cdn/12.15.1/img/profileicon/" + profileIconId + ".png";

        return url;
    }

    public String callTierIcon(String tier) {
        String url = "https://opgg-static.akamaized.net/images/medals_new/" + tier + ".png?image=q_auto,f_webp,w_144&v=1660126953027";

        return url;
    }

    public String callItemIcon(String item) {
        String url = "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/item/" + item + ".png";

        if (item.equals("0")) {
            url = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Regular_quadrilateral.svg/220px-Regular_quadrilateral.svg.png";
        }

        return url;
    }

    public String callChampionIcon(String champion) {
        String url = "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/champion/" + champion + ".png";

        if (champion.equals("FiddleSticks")) {
            url = "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/champion/" + "Fiddlesticks" + ".png";
        }

        return url;
    }

    public String callPrimaryPerkIcon(String perk) {
        String url = "https://opgg-static.akamaized.net/images/lol/perk/" + perk + ".png?image=q_auto,f_webp,w_auto&v=1660789334950";

        return url;
    }

    public String callSubPerkIcon(String perk) {
        String url = "https://opgg-static.akamaized.net/images/lol/perkStyle/" + perk + ".png?image=q_auto,f_webp,w_auto&v=1660789334950";

        return url;
    }

    String spellUrl;
    public void findUrl(String url) {
        spellUrl = url;
    }
    public String callSpellIcon(String spellId) {
        String url = "http://ddragon.leagueoflegends.com/cdn/12.15.1/data/en_US/summoner.json";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
                JSONObject data = (JSONObject)  jsonObject.get("data");

                data.keySet().forEach( key->{
                            JSONObject spellInfo = (JSONObject) data.get(key);

                            if(spellInfo.get("key").equals(spellId)) {
                                JSONObject image = (JSONObject) spellInfo.get("image");
                                String spell = "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/spell/" + image.get("full").toString();
                                findUrl(spell);
                            }
                        }
                );
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return spellUrl;
    }
}
