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
    private IconService iconService = new IconService();

    @Value("${riotApiKey}")
    private String myKey;

    public SummonerDTO callRiotAPISummonerByName(String summonerName) {
        SummonerDTO summonerDTO = new SummonerDTO();
        String serverUrl = "https://kr.api.riotgames.com";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(serverUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + myKey);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);

                summonerDTO.setName(jsonObject.get("name").toString());
                summonerDTO.setId(jsonObject.get("id").toString());
                summonerDTO.setPuuid(jsonObject.get("puuid").toString());
                summonerDTO.setProfileIcon(iconService.callSummonerIcon(jsonObject.get("profileIconId").toString()));
                summonerDTO.setSummonerLevel(Long.parseLong(jsonObject.get("summonerLevel").toString()));
            } else {
                System.out.println("error : " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

        return summonerDTO;
    }

    public List<String> callMatchHistory(String puuid, int start) {
        List<String> result = new ArrayList();
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url + puuid + "/ids?start=" + start + "&count=5&api_key=" + myKey);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(body);

                for (int i = 0; i < jsonArray.size(); i++) {
                    result.add(jsonArray.get(i).toString());
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public LeagueEntryDTO callLeagueEntry(String id) {
        LeagueEntryDTO leagueEntryDTO = new LeagueEntryDTO();
        String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url + id + "?api_key=" + myKey);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(body);
                if (jsonArray.size() != 0) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                    leagueEntryDTO.setTier(jsonObject.get("tier").toString());
                    leagueEntryDTO.setTierUrl(iconService.callTierIcon(jsonObject.get("tier").toString()));
                    leagueEntryDTO.setRank(jsonObject.get("rank").toString());
                    leagueEntryDTO.setLeaguePoints(Integer.parseInt(jsonObject.get("leaguePoints").toString()));
                    leagueEntryDTO.setWins(Integer.parseInt(jsonObject.get("wins").toString()));
                    leagueEntryDTO.setLosses(Integer.parseInt(jsonObject.get("losses").toString()));
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

        return leagueEntryDTO;
    }

    public List<MatchDTO> callMatchAbout(List<String> matchHistory, String summonerName) {
        List<MatchDTO> matchDTOs = new ArrayList<>();

        for (String matchId : matchHistory) {
            String url = "https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId + "?api_key=" + myKey;
            MatchDTO matchDTO = new MatchDTO();

            matchDTO.setMatchId(matchId);

            try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);

                if (response.getStatusLine().getStatusCode() == 200) {
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
                    } else if (timestamp * 24 >= 60) {
                        String endTime = Math.round(timestamp) + "시간 전";
                        matchDTO.setEndTime(endTime);
                    } else {
                        String endTime = Math.round(timestamp) + "분 전";
                        matchDTO.setEndTime(endTime);
                    }


                    Long gameDuration = Long.parseLong(info.get("gameDuration").toString());
                    matchDTO.setGameDurationMinutes(gameDuration / 60);
                    matchDTO.setGameDurationSeconds(gameDuration % 60);

                    MyInfoDTO myInfoDTO = new MyInfoDTO();
                    for (int i = 0; i < participants.size(); i++) {
                        JSONObject participant = (JSONObject) participants.get(i);

                        if (participant.get("summonerName").toString().equalsIgnoreCase(summonerName)) {
                            matchDTO.setWin((boolean) participant.get("win"));
                            myInfoDTO.setChampionName(iconService.callChampionIcon(participant.get("championName").toString()));

                            List<String> myItems = new ArrayList<>();
                            for (int j = 0; j <= 6; j++) {
                                myItems.add(iconService.callItemIcon(participant.get("item" + j).toString()));
                            }
                            myInfoDTO.setItems(myItems);

                            myInfoDTO.setKills(Integer.parseInt(participant.get("kills").toString()));
                            myInfoDTO.setDeaths(Integer.parseInt(participant.get("deaths").toString()));
                            myInfoDTO.setAssists(Integer.parseInt(participant.get("assists").toString()));
                            myInfoDTO.setSpell1Id(iconService.callSpellIcon(participant.get("summoner1Id").toString()));
                            myInfoDTO.setSpell2Id(iconService.callSpellIcon(participant.get("summoner2Id").toString()));

                            JSONObject perks = (JSONObject) participant.get("perks");
                            JSONArray styles = (JSONArray) perks.get("styles");
                            JSONObject primaryStyle = (JSONObject) styles.get(0);
                            JSONObject subStyle = (JSONObject) styles.get(1);
                            JSONArray primarySelections = (JSONArray) primaryStyle.get("selections");
                            JSONObject primarySelectionsNumber = (JSONObject) primarySelections.get(0);

                            myInfoDTO.setPrimaryPerk(iconService.callPrimaryPerkIcon(primarySelectionsNumber.get("perk").toString()));
                            myInfoDTO.setSubPerk(iconService.callSubPerkIcon(subStyle.get("style").toString()));
                        }
                    }

                    matchDTO.setMyInfoDTO(myInfoDTO);
                } else {
                    System.out.println("error : " + response.getStatusLine().getStatusCode());
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                return null;
            }

            matchDTOs.add(matchDTO);
        }

        return matchDTOs;
    }

    public MatchDTO callDetailMatch(String matchId) {
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId + "?api_key=" + myKey;
        MatchDTO matchDTO = new MatchDTO();

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
                JSONObject info = (JSONObject) jsonObject.get("info");
                JSONArray participants = (JSONArray) info.get("participants");

                List<MatchUserInfoDTO> matchUserInfoDTOs = new ArrayList<>();
                for (int i = 0; i < participants.size(); i++) {
                    JSONObject participant = (JSONObject) participants.get(i);
                    MatchUserInfoDTO matchUserInfoDTO = new MatchUserInfoDTO();

                    matchUserInfoDTO.setSummonerName(participant.get("summonerName").toString());

                    matchUserInfoDTO.setWin((boolean) participant.get("win"));
                    matchUserInfoDTO.setChampionName(iconService.callChampionIcon(participant.get("championName").toString()));
                    matchUserInfoDTO.setKills(Integer.parseInt(participant.get("kills").toString()));
                    matchUserInfoDTO.setDeaths(Integer.parseInt(participant.get("deaths").toString()));
                    matchUserInfoDTO.setAssists(Integer.parseInt(participant.get("assists").toString()));
                    matchUserInfoDTO.setTotalMinionsKilled(Integer.parseInt(participant.get("totalMinionsKilled").toString()));
                    matchUserInfoDTO.setTotalDamageDealtToChampions(Integer.parseInt(participant.get("totalDamageDealtToChampions").toString()));

                    List<String> items = new ArrayList<>();

                    for (int j = 0; j <= 6; j++) {
                        items.add(iconService.callItemIcon(participant.get("item" + j).toString()));
                    }

                    matchUserInfoDTO.setItems(items);

                    JSONObject perks = (JSONObject) participant.get("perks");
                    JSONArray styles = (JSONArray) perks.get("styles");
                    JSONObject primaryStyle = (JSONObject) styles.get(0);
                    JSONObject subStyle = (JSONObject) styles.get(1);
                    JSONArray primarySelections = (JSONArray) primaryStyle.get("selections");
                    JSONObject primarySelectionsNumber = (JSONObject) primarySelections.get(0);

                    matchUserInfoDTO.setPrimaryPerk(iconService.callPrimaryPerkIcon(primarySelectionsNumber.get("perk").toString()));
                    matchUserInfoDTO.setSubPerk(iconService.callSubPerkIcon(subStyle.get("style").toString()));
                    matchUserInfoDTO.setSpell1Id(iconService.callSpellIcon(participant.get("summoner1Id").toString()));
                    matchUserInfoDTO.setSpell2Id(iconService.callSpellIcon(participant.get("summoner2Id").toString()));

                    matchUserInfoDTOs.add(matchUserInfoDTO);
                }

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
