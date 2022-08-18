package com.example.lol.service;

import com.example.lol.dto.LeagueEntryDTO;
import com.example.lol.dto.MatchDTO;
import com.example.lol.dto.MatchUserInfoDTO;
import com.example.lol.dto.SummonerDTO;
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
            HttpGet request = new HttpGet(url + puuid + "/ids?start=0&count=5&api_key=" + myKey);

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

                Long gameDuration = Long.parseLong(info.get("gameDuration").toString());
                matchDTO.setGameDurationMinutes(gameDuration/60);
                matchDTO.setGameDurationSeconds(gameDuration%60);

                List<MatchUserInfoDTO> matchUserInfoDTOs = new ArrayList<>();
                for(int i=0;i<participants.size();i++){
                    JSONObject participant = (JSONObject) participants.get(i);
                    MatchUserInfoDTO matchUserInfoDTO = new MatchUserInfoDTO();

                    matchUserInfoDTO.setSummonerName(participant.get("summonerName").toString());
                    if(participant.get("summonerName").toString().equals(summonerName)) {
                        matchDTO.setWin((boolean) participant.get("win"));
                    }
                    matchUserInfoDTO.setWin((boolean) participant.get("win"));
                    matchUserInfoDTO.setChampionName(participant.get("championName").toString());
                    matchUserInfoDTO.setKills(Integer.parseInt(participant.get("kills").toString()));
                    matchUserInfoDTO.setDeaths(Integer.parseInt(participant.get("deaths").toString()));
                    matchUserInfoDTO.setAssists(Integer.parseInt(participant.get("assists").toString()));
                    matchUserInfoDTO.setTotalMinionsKilled(Integer.parseInt(participant.get("totalMinionsKilled").toString()));
                    matchUserInfoDTO.setTotalDamageDealtToChampions(Integer.parseInt(participant.get("totalDamageDealtToChampions").toString()));
                    matchUserInfoDTO.setItem0(Integer.parseInt(participant.get("item0").toString()));
                    matchUserInfoDTO.setItem1(Integer.parseInt(participant.get("item1").toString()));
                    matchUserInfoDTO.setItem2(Integer.parseInt(participant.get("item2").toString()));
                    matchUserInfoDTO.setItem3(Integer.parseInt(participant.get("item3").toString()));
                    matchUserInfoDTO.setItem4(Integer.parseInt(participant.get("item4").toString()));
                    matchUserInfoDTO.setItem5(Integer.parseInt(participant.get("item5").toString()));
                    matchUserInfoDTO.setItem6(Integer.parseInt(participant.get("item6").toString()));

                    JSONObject perks = (JSONObject) participant.get("perks");
                    JSONArray styles = (JSONArray) perks.get("styles");
                    JSONObject primaryStyle = (JSONObject) styles.get(0);
                    JSONObject subStyle = (JSONObject) styles.get(1);
                    JSONArray primarySelections = (JSONArray) primaryStyle.get("selections");
                    JSONObject primarySelectionsNumber = (JSONObject) primarySelections.get(0);

                    matchUserInfoDTO.setPrimaryPerk(Integer.parseInt(primarySelectionsNumber.get("perk").toString()));
                    matchUserInfoDTO.setSubPerk(Integer.parseInt(subStyle.get("style").toString()));

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
