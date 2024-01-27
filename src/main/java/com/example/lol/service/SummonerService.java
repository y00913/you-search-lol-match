package com.example.lol.service;

import com.example.lol.dto.*;
import com.example.lol.repository.*;
import com.example.lol.util.QueueType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@PropertySource(value = "classpath:riotApiKey.properties")
public class SummonerService {

    @Autowired
    private SummonerRepository summonerRepository;

    @Autowired
    private RankTypeRepository rankTypeRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Value("${riotApiKey}")
    private String myKey;

    @Transactional
    public Summoner callRiotAPISummonerByPuuid(RiotInfo riotInfo) {
        Summoner summonerDTO = summonerRepository.findByNameAndTagLine(riotInfo.getName(), riotInfo.getTagLine()).orElse(new Summoner());

        if(summonerDTO.getUpdateAt() != null && ChronoUnit.MINUTES.between(summonerDTO.getUpdateAt(), LocalDateTime.now()) < 10){
            throw new RuntimeException("조회한지 10분이 지나지 않았습니다.");
        }

        String summonerName = riotInfo.getName().replaceAll(" ", "%20");
        String infoUrl = "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/" + summonerName + "/" + riotInfo.getTagLine();

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(infoUrl + "?api_key=" + myKey);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);

                summonerDTO.setName(jsonObject.get("gameName").toString());
                summonerDTO.setTagLine(jsonObject.get("tagLine").toString());
                summonerDTO.setPuuid(jsonObject.get("puuid").toString());
            } else {
                System.out.println("error : " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        String lolUrl = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/" + summonerDTO.getPuuid();

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(lolUrl + "?api_key=" + myKey);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);

                summonerDTO.setId(jsonObject.get("id").toString());
                summonerDTO.setProfileIcon(jsonObject.get("profileIconId").toString());
                summonerDTO.setSummonerLevel(Long.parseLong(jsonObject.get("summonerLevel").toString()));
                summonerDTO.setUpdateAt(LocalDateTime.now());

                summonerRepository.save(summonerDTO);
            } else {
                System.out.println("error : " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return summonerDTO;
    }

    public List<String> callMatchHistory(String puuid, Long startTime) {
        List<String> result = new ArrayList();
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid;

        try {
            WebClient webClient = WebClient.builder().baseUrl(url).build();
            List<String> body = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/ids")
                            .queryParam("startTime", startTime)
                            .queryParam("start", 0)
                            .queryParam("count", 30)
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

    @Transactional
    public RankType callRankTier(String id) {
        String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id;
        RankType rankType = new RankType();

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

            rankType.setId(id);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                if (jsonObject.get("queueType").toString().equals("RANKED_FLEX_SR")) {
                    rankType.setFlexUserTier(jsonObject.get("tier").toString());
                    rankType.setFlexUserRank(jsonObject.get("rank").toString());
                    rankType.setFlexLeaguePoints(Integer.parseInt(jsonObject.get("leaguePoints").toString()));
                    rankType.setFlexUserWins(Integer.parseInt(jsonObject.get("wins").toString()));
                    rankType.setFlexUserLosses(Integer.parseInt(jsonObject.get("losses").toString()));
                } else if (jsonObject.get("queueType").toString().equals("RANKED_SOLO_5x5")) {
                    rankType.setSoloUserTier(jsonObject.get("tier").toString());
                    rankType.setSoloUserRank(jsonObject.get("rank").toString());
                    rankType.setSoloLeaguePoints(Integer.parseInt(jsonObject.get("leaguePoints").toString()));
                    rankType.setSoloUserWins(Integer.parseInt(jsonObject.get("wins").toString()));
                    rankType.setSoloUserLosses(Integer.parseInt(jsonObject.get("losses").toString()));
                }
            }

            rankTypeRepository.save(rankType);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return rankType;
    }

    @Transactional
    public void callMatchAbout(List<String> matchHistory, String puuid) {
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

                String qt = QueueType.findQueueType(info.get("queueId").toString());
                if (qt.equals("기타")) {
                    continue;
                }
                matchDTO.setQueueType(qt);
                matchDTO.setEndTimeStamp(Long.parseLong(info.get("gameEndTimestamp").toString()) / 1000);

                Long gameDuration = Long.parseLong(info.get("gameDuration").toString());
                matchDTO.setGameDurationMinutes(gameDuration / 60);
                matchDTO.setGameDurationSeconds(gameDuration % 60);

                for (int i = 0; i < participants.size(); i++) {
                    JSONObject participant = (JSONObject) participants.get(i);

                    if (participant.get("puuid").toString().equalsIgnoreCase(puuid)) {
                        matchDTO.setWin((boolean) participant.get("win"));
                        matchDTO.setChampionName(participant.get("championName").toString());

                        matchDTO.setItem0(participant.get("item0").toString());
                        matchDTO.setItem1(participant.get("item1").toString());
                        matchDTO.setItem2(participant.get("item2").toString());
                        matchDTO.setItem3(participant.get("item3").toString());
                        matchDTO.setItem4(participant.get("item4").toString());
                        matchDTO.setItem5(participant.get("item5").toString());
                        matchDTO.setItem6(participant.get("item6").toString());

                        matchDTO.setKills(Integer.parseInt(participant.get("kills").toString()));
                        matchDTO.setDeaths(Integer.parseInt(participant.get("deaths").toString()));
                        matchDTO.setAssists(Integer.parseInt(participant.get("assists").toString()));
                        matchDTO.setSpell1Id(participant.get("summoner1Id").toString());
                        matchDTO.setSpell2Id(participant.get("summoner2Id").toString());

                        JSONObject perks = (JSONObject) participant.get("perks");
                        JSONArray styles = (JSONArray) perks.get("styles");
                        JSONObject primaryStyle = (JSONObject) styles.get(0);
                        JSONObject subStyle = (JSONObject) styles.get(1);
                        JSONArray primarySelections = (JSONArray) primaryStyle.get("selections");
                        JSONObject primarySelectionsNumber = (JSONObject) primarySelections.get(0);

                        matchDTO.setPrimaryPerk(primarySelectionsNumber.get("perk").toString());
                        matchDTO.setSubPerk(subStyle.get("style").toString());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            matchDTOs.add(matchDTO);
        }

        matchRepository.saveAll(matchDTOs);
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

                matchUserInfoDTO.setSummonerName(participant.get("riotIdGameName").toString());
                matchUserInfoDTO.setTagLine(participant.get("riotIdTagline").toString());

                matchUserInfoDTO.setWin((boolean) participant.get("win"));
                matchUserInfoDTO.setChampionName(participant.get("championName").toString());
                matchUserInfoDTO.setChampLevel(Integer.parseInt(participant.get("champLevel").toString()));
                matchUserInfoDTO.setKills(Integer.parseInt(participant.get("kills").toString()));
                matchUserInfoDTO.setDeaths(Integer.parseInt(participant.get("deaths").toString()));
                matchUserInfoDTO.setAssists(Integer.parseInt(participant.get("assists").toString()));
                matchUserInfoDTO.setTotalMinionsKilled(Integer.parseInt(participant.get("totalMinionsKilled").toString()));
                matchUserInfoDTO.setTotalDamageDealtToChampions(Integer.parseInt(participant.get("totalDamageDealtToChampions").toString()));

                List<String> items = new ArrayList<>();

                for (int j = 0; j <= 6; j++) {
                    items.add(participant.get("item" + j).toString());
                }

                matchUserInfoDTO.setItems(items);

                JSONObject perks = (JSONObject) participant.get("perks");
                JSONArray styles = (JSONArray) perks.get("styles");
                JSONObject primaryStyle = (JSONObject) styles.get(0);
                JSONObject subStyle = (JSONObject) styles.get(1);
                JSONArray primarySelections = (JSONArray) primaryStyle.get("selections");
                JSONObject primarySelectionsNumber = (JSONObject) primarySelections.get(0);

                matchUserInfoDTO.setPrimaryPerk(primarySelectionsNumber.get("perk").toString());
                matchUserInfoDTO.setSubPerk(subStyle.get("style").toString());
                matchUserInfoDTO.setSpell1Id(participant.get("summoner1Id").toString());
                matchUserInfoDTO.setSpell2Id(participant.get("summoner2Id").toString());

                matchUserInfoDTOs.add(matchUserInfoDTO);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return matchUserInfoDTOs;
    }

    public Optional<Summoner> findSummoner(RiotInfo riotInfo){
        return summonerRepository.findByNameAndTagLine(riotInfo.getName(), riotInfo.getTagLine());
    }

    public Boolean checkMatch(String puuid) {
        return matchRepository.existsByPuuid(puuid);
    }

    public Optional<RankType> findRankType(String id) {
        return rankTypeRepository.findById(id);
    }

    public Page<Match> findPagingMatch(String puuid, int start){
        return matchRepository.findByPuuid(PageRequest.of(start,10, Sort.Direction.DESC, "endTimeStamp"), puuid);
    }

    public Optional<StartTimeMapping> getStartTime(String puuid){
        return matchRepository.findTop1ByPuuid(puuid, Sort.by(Sort.Direction.DESC, "endTimeStamp"));
    }

}
