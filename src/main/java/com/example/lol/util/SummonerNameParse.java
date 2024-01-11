package com.example.lol.util;

import com.example.lol.dto.RiotInfo;

public class SummonerNameParse {

    public static RiotInfo getNameAndTage(String summonerName) {
        if(!summonerName.contains("#")) {
            summonerName += "#KR1";
        }

        int index = summonerName.indexOf("#");

        String name = summonerName.substring(0, index);
        String tagLine = summonerName.substring(index + 1);

        return new RiotInfo(name, tagLine);
    }

}
