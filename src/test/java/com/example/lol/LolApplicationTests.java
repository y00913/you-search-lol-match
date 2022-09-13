package com.example.lol;

import com.example.lol.dto.*;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@PropertySource(value = "classpath:riotApiKey.properties")
class LolApplicationTests {

    @Value("${riotApiKey}")
    private String myKey;

    @Test
    void contextLoads() {
        long timestamp = (System.currentTimeMillis() - Long.parseLong("1661521220421")) / 1000;

        System.out.println(timestamp / 60 + " 분 ");
        System.out.println(timestamp / 60 / 60 + " 시 ");
        System.out.println(timestamp / 60 / 60 / 24 + " 일 ");
    }

    @Test
    public void callRiotAPISummonerByName() {
        Summoner summonerDTO = new Summoner();
        String serverUrl = "https://kr.api.riotgames.com";
        String summonerName = "hide%20on%20bush";

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
                summonerDTO.setSummonerLevel(Long.parseLong(jsonObject.get("summonerLevel").toString()));
            } else {
                System.out.println("error : " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println(summonerDTO);
    }

    @Test
    public List<String> callMatchHistory() {
        String puuid = "zohfVkLTI7DpNsI6-JU5GzsAUQACJTiN6XV8nCIKkJS42lyDUFU4gG-OdGtoHx1JxMopLmXXNXUSMQ";
        List<String> result = new ArrayList();
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url + puuid + "/ids?start=0&count=10&api_key=" + myKey);
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
        }

        return result;
    }

    @Test
    public List<String> test() throws ParseException {
        String puuid = "zohfVkLTI7DpNsI6-JU5GzsAUQACJTiN6XV8nCIKkJS42lyDUFU4gG-OdGtoHx1JxMopLmXXNXUSMQ";
        List<String> result = new ArrayList();
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/";

        WebClient webClient = WebClient.builder().baseUrl(url + puuid).build();
        List<String> body = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/ids")
                        .queryParam("start", 0)
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

        return result;
    }

    @Test
    public void test2() {
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/KR_6097853832";

        WebClient webClient = WebClient.builder().baseUrl(url).build();
        List<String> result = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("")
                        .queryParam("api_key", myKey).build())
                .retrieve()
                .bodyToFlux(String.class)
                .toStream()
                .collect(Collectors.toList());

        System.out.println(result);
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

    public void getRes(ClientResponse res) {
        response = res;
    }

    public ClientResponse response;

    @Test
    public void test4() {
        String summonerName = "ㅁㅁ";
        Summoner summonerDTO = new Summoner();
        String url = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName;

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

            summonerDTO.setName(jsonObject.get("name").toString());
            summonerDTO.setId(jsonObject.get("id").toString());
            summonerDTO.setPuuid(jsonObject.get("puuid").toString());
            summonerDTO.setSummonerLevel(Long.parseLong(jsonObject.get("summonerLevel").toString()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(summonerDTO);
    }

}
