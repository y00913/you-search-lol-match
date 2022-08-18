package com.example.lol.service;

import com.example.lol.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@PropertySource(value = "classpath:riotApiKey.properties")
public class SummonerService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${riotApiKey}")
    private String myKey;

    public SummonerDTO callRiotAPISummonerByName(String summonerName) {
        SummonerDTO result;

        String serverUrl = "https://kr.api.riotgames.com";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(serverUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + myKey);

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            HttpEntity entity = response.getEntity();
            result = objectMapper.readValue(entity.getContent(), SummonerDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public List<String> callMatchHistory(String puuid) {
        List<String> result = new ArrayList();

        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url + puuid + "/ids?start=0&count=7&api_key=" + myKey);

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            HttpEntity entity = response.getEntity();
            result = objectMapper.readValue(entity.getContent(), ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public List<LeagueEntryDTO> callLeagueEntry(String id) {
        Set<LeagueEntryDTO> tmp = new HashSet<>();

        String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url + id + "?api_key=" + myKey);

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            HttpEntity entity = response.getEntity();
            tmp = objectMapper.readValue(entity.getContent(), Set.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<LeagueEntryDTO> result = objectMapper.convertValue(tmp, new TypeReference<List<LeagueEntryDTO>>() {
        });

        return result;
    }

    public MatchDTO callMatchAbout(String matchId, String summonerName) {
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId + "?api_key=" + myKey;
        MatchDTO matchDTO = new MatchDTO();
        IconService iconService = new IconService();

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if(response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
                JSONObject info = (JSONObject) jsonObject.get("info");
                JSONArray participants = (JSONArray) info.get("participants");

                matchDTO.setGameMode(info.get("gameMode").toString());

                long timestamp = (System.currentTimeMillis() - Long.parseLong(info.get("gameEndTimestamp").toString())) / 1000 / 60 / 60;

                if (timestamp >= 24) {
                    String endTime = Math.round(timestamp / 24) + "일 전";
                    matchDTO.setEndTime(endTime);
                } else {
                    String endTime = Math.round(timestamp) + "시간 전";
                    matchDTO.setEndTime(endTime);
                }


                Long gameDuration = Long.parseLong(info.get("gameDuration").toString());
                matchDTO.setGameDurationMinutes(gameDuration/60);
                matchDTO.setGameDurationSeconds(gameDuration%60);

                List<MatchUserInfoDTO> matchUserInfoDTOs = new ArrayList<>();
                MyInfoDTO myInfoDTO = new MyInfoDTO();
                for(int i=0;i<participants.size();i++){
                    JSONObject participant = (JSONObject) participants.get(i);
                    MatchUserInfoDTO matchUserInfoDTO = new MatchUserInfoDTO();

                    matchUserInfoDTO.setSummonerName(participant.get("summonerName").toString());

                    if(participant.get("summonerName").toString().equalsIgnoreCase(summonerName)) {
                        matchDTO.setWin((boolean) participant.get("win"));
                        myInfoDTO.setChampionName(iconService.callChampionIcon(participant.get("championName").toString()));

                        List<String> myItems = new ArrayList<>();
                        for(int j=0;j<=6;j++){
                            myItems.add(iconService.callItemIcon(participant.get("item" + j).toString()));
                        }
                        myInfoDTO.setItems(myItems);
                    }
                    matchUserInfoDTO.setWin((boolean) participant.get("win"));
                    matchUserInfoDTO.setChampionName(iconService.callChampionIcon(participant.get("championName").toString()));
                    matchUserInfoDTO.setKills(Integer.parseInt(participant.get("kills").toString()));
                    matchUserInfoDTO.setDeaths(Integer.parseInt(participant.get("deaths").toString()));
                    matchUserInfoDTO.setAssists(Integer.parseInt(participant.get("assists").toString()));
                    matchUserInfoDTO.setTotalMinionsKilled(Integer.parseInt(participant.get("totalMinionsKilled").toString()));
                    matchUserInfoDTO.setTotalDamageDealtToChampions(Integer.parseInt(participant.get("totalDamageDealtToChampions").toString()));

                    List<String> items = new ArrayList<>();

                    for(int j=0;j<=6;j++){
                        items.add(iconService.callItemIcon(participant.get("item" + j).toString()));
                    }

                    matchUserInfoDTO.setItems(items);

                    JSONObject perks = (JSONObject) participant.get("perks");
                    JSONArray styles = (JSONArray) perks.get("styles");
                    JSONObject primaryStyle = (JSONObject) styles.get(0);
                    JSONObject subStyle = (JSONObject) styles.get(1);
                    JSONArray primarySelections = (JSONArray) primaryStyle.get("selections");
                    JSONObject primarySelectionsNumber = (JSONObject) primarySelections.get(0);

                    if(participant.get("summonerName").toString().equalsIgnoreCase(summonerName)){
                        myInfoDTO.setPrimaryPerk(iconService.callPrimaryPerkIcon(primarySelectionsNumber.get("perk").toString()));
                        myInfoDTO.setSubPerk(iconService.callSubPerkIcon(subStyle.get("style").toString()));
                    }

                    matchUserInfoDTO.setPrimaryPerk(iconService.callPrimaryPerkIcon(primarySelectionsNumber.get("perk").toString()));
                    matchUserInfoDTO.setSubPerk(iconService.callSubPerkIcon(subStyle.get("style").toString()));

                    matchUserInfoDTOs.add(matchUserInfoDTO);
                }

                matchDTO.setMyInfoDTO(myInfoDTO);
                matchDTO.setMatchUserInfoDTOs(matchUserInfoDTOs);
            } else {
                System.out.println("error : " + response.getStatusLine().getStatusCode());
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }

        return matchDTO;
    }

}
