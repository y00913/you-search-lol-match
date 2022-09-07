package com.example.lol.service;

import com.example.lol.dto.*;
import com.example.lol.repository.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@PropertySource(value = "classpath:riotApiKey.properties")
public class SummonerService {
    @Autowired
    private IconService iconService;

    @Autowired
    SummonerRepository summonerRepository;

    @Autowired
    RankTypeFlexRepository rankTypeFlexRepository;

    @Autowired
    RankTypeSoloRepository rankTypeSoloRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MyInfoRepository myInfoRepository;

    @Value("${riotApiKey}")
    private String myKey;

    public Summoner callRiotAPISummonerByName(String summonerName) {
        Summoner summonerDTO = new Summoner();
        String url = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url + "?api_key=" + myKey);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);

                summonerDTO.setName(jsonObject.get("name").toString());
                summonerDTO.setId(jsonObject.get("id").toString());
                summonerDTO.setPuuid(jsonObject.get("puuid").toString());
                summonerDTO.setProfileIcon(jsonObject.get("profileIconId").toString());
                summonerDTO.setSummonerLevel(Long.parseLong(jsonObject.get("summonerLevel").toString()));

                if(summonerRepository.findByPuuid(summonerDTO.getPuuid()) == null) {
                    summonerRepository.save(summonerDTO);
                }
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
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid;

        try {
            WebClient webClient = WebClient.builder().baseUrl(url).build();
            List<String> body = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/ids")
                            .queryParam("start", start)
                            .queryParam("count", 10)
                            .queryParam("api_key", myKey).build())
                    .retrieve()
                    .bodyToFlux(String.class)
                    .toStream()
                    .collect(Collectors.toList());

            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body.get(0));


            for (int i = 0; i < jsonArray.size(); i++) {
                result.add(jsonArray.get(i).toString());
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public LeagueEntry callLeagueEntry(String id) {
        LeagueEntry leagueEntryDTO = new LeagueEntry();
        String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id;

        try {
            WebClient webClient = WebClient.builder().baseUrl(url).build();
            List<String> body = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("")
                            .queryParam("api_key", myKey).build())
                    .retrieve()
                    .bodyToFlux(String.class)
                    .toStream()
                    .collect(Collectors.toList());

            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body.get(0));

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                RankTypeFlex rankTypeFlex = new RankTypeFlex();
                RankTypeSolo rankTypeSolo = new RankTypeSolo();

                if (jsonObject.get("queueType").toString().equals("RANKED_FLEX_SR")) {
                    rankTypeFlex.setId(id);
                    rankTypeFlex.setQueueType(jsonObject.get("queueType").toString());
                    rankTypeFlex.setUserTier(jsonObject.get("tier").toString());
                    rankTypeFlex.setUserTierUrl(iconService.callTierIcon(jsonObject.get("tier").toString()));
                    rankTypeFlex.setUserRank(jsonObject.get("rank").toString());
                    rankTypeFlex.setLeaguePoints(Integer.parseInt(jsonObject.get("leaguePoints").toString()));
                    rankTypeFlex.setUserWins(Integer.parseInt(jsonObject.get("wins").toString()));
                    rankTypeFlex.setUserLosses(Integer.parseInt(jsonObject.get("losses").toString()));

                    leagueEntryDTO.setRanked_Flex(rankTypeFlex);
                    if(rankTypeFlexRepository.findById(id) == null){
                        rankTypeFlexRepository.save(rankTypeFlex);
                    }
                } else {
                    rankTypeSolo.setId(id);
                    rankTypeSolo.setQueueType(jsonObject.get("queueType").toString());
                    rankTypeSolo.setUserTier(jsonObject.get("tier").toString());
                    rankTypeSolo.setUserTierUrl(iconService.callTierIcon(jsonObject.get("tier").toString()));
                    rankTypeSolo.setUserRank(jsonObject.get("rank").toString());
                    rankTypeSolo.setLeaguePoints(Integer.parseInt(jsonObject.get("leaguePoints").toString()));
                    rankTypeSolo.setUserWins(Integer.parseInt(jsonObject.get("wins").toString()));
                    rankTypeSolo.setUserLosses(Integer.parseInt(jsonObject.get("losses").toString()));

                    leagueEntryDTO.setRanked_Solo(rankTypeSolo);
                    if(rankTypeSoloRepository.findById(id) == null){
                        rankTypeSoloRepository.save(rankTypeSolo);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return leagueEntryDTO;
    }

    public List<Match> callMatchAbout(List<String> matchHistory, String puuid) {
        List<Match> matchDTOs = new ArrayList<>();

        for (String matchId : matchHistory) {
            String url = "https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId + "?api_key=" + myKey;
            Match matchDTO = new Match();

            matchDTO.setPuuid(puuid);
            matchDTO.setMatchId(matchId);

            try {
                WebClient webClient = WebClient.builder().baseUrl(url).build();
                List<String> body = webClient.get()
                        .uri(uriBuilder -> uriBuilder.path("")
                                .queryParam("api_key", myKey).build())
                        .retrieve()
                        .bodyToFlux(String.class)
                        .toStream()
                        .collect(Collectors.toList());

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body.get(0));
                JSONObject info = (JSONObject) jsonObject.get("info");
                JSONArray participants = (JSONArray) info.get("participants");

                String qt = findQueueType(info.get("queueId").toString());
                if (qt.equals("기타")) {
                    continue;
                }
                matchDTO.setQueueType(qt);
                matchDTO.setEndTimeStamp(Long.parseLong(info.get("gameEndTimestamp").toString()));

                long timestamp = (System.currentTimeMillis() - Long.parseLong(info.get("gameEndTimestamp").toString())) / 1000;
                if (timestamp < 60) {
                    String endTime = Math.round(timestamp) + "초 전";
                    matchDTO.setEndTime(endTime);
                } else if (timestamp / 60 < 60) {
                    String endTime = Math.round(timestamp / 60) + "분 전";
                    matchDTO.setEndTime(endTime);
                } else if (timestamp / 60 / 60 < 24) {
                    String endTime = Math.round(timestamp / 60 / 60) + "시간 전";
                    matchDTO.setEndTime(endTime);
                } else if (timestamp / 60 / 60 / 24 < 30) {
                    String endTime = Math.round(timestamp / 60 / 60 / 24) + "일 전";
                    matchDTO.setEndTime(endTime);
                } else {
                    String endTime = Math.round(timestamp / 60 / 60 / 24 / 30) + "달 전";
                    matchDTO.setEndTime(endTime);
                }

                Long gameDuration = Long.parseLong(info.get("gameDuration").toString());
                matchDTO.setGameDurationMinutes(gameDuration / 60);
                matchDTO.setGameDurationSeconds(gameDuration % 60);

                MyInfo myInfoDTO = new MyInfo();
                for (int i = 0; i < participants.size(); i++) {
                    JSONObject participant = (JSONObject) participants.get(i);

                    if (participant.get("puuid").toString().equalsIgnoreCase(puuid)) {
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

                if(matchRepository.findByMatchId(matchId) == null){
                    myInfoRepository.save(myInfoDTO);
                    matchRepository.save(matchDTO);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

            matchDTOs.add(matchDTO);

        }

        return matchDTOs;
    }

    public List<MatchUserInfo> callDetailMatch(String matchId) {
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId;
        List<MatchUserInfo> matchUserInfoDTOs = new ArrayList<>();

        try {
            WebClient webClient = WebClient.builder().baseUrl(url).build();
            List<String> body = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("")
                            .queryParam("api_key", myKey).build())
                    .retrieve()
                    .bodyToFlux(String.class)
                    .toStream()
                    .collect(Collectors.toList());

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(body.get(0));
            JSONObject info = (JSONObject) jsonObject.get("info");
            JSONArray participants = (JSONArray) info.get("participants");

            for (int i = 0; i < participants.size(); i++) {
                JSONObject participant = (JSONObject) participants.get(i);
                MatchUserInfo matchUserInfoDTO = new MatchUserInfo();

                matchUserInfoDTO.setSummonerName(participant.get("summonerName").toString());

                matchUserInfoDTO.setWin((boolean) participant.get("win"));
                matchUserInfoDTO.setChampionName(iconService.callChampionIcon(participant.get("championName").toString()));
                matchUserInfoDTO.setChampLevel(Integer.parseInt(participant.get("champLevel").toString()));
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
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return matchUserInfoDTOs;
    }

    public String findQueueType(String queueId) {
        String result = "";

        if (queueId.equals("400") || queueId.equals("430")) {
            result = "일반";
        } else if (queueId.equals("420")) {
            result = "솔랭";
        } else if (queueId.equals("440")) {
            result = "자유 랭크";
        } else if (queueId.equals("450")) {
            result = "무작위 총력전";
        } else if (queueId.equals("700")) {
            result = "격전";
        } else if (queueId.equals("800") || queueId.equals("810") || queueId.equals("820") || queueId.equals("830") || queueId.equals("840") || queueId.equals("850")) {
            result = "AI 대전";
        } else if (queueId.equals("900")) {
            result = "URF 모드";
        } else if (queueId.equals("920")) {
            result = "전설의 포로왕";
        } else if (queueId.equals("1020")) {
            result = "단일 챔피언";
        } else if (queueId.equals("1300")) {
            result = "돌격! 넥서스";
        } else if (queueId.equals("1400")) {
            result = "궁극기 주문서";
        } else {
            result = "기타";
        }

        return result;
    }

}
