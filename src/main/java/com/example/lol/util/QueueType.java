package com.example.lol.util;

public class QueueType {

    public static String findQueueType(String queueId){
        String result = "";

        if (queueId.equals("400") || queueId.equals("430")) {
            result = "일반";
        } else if (queueId.equals("490")){
            result = "빠른 대전";
        }  else if (queueId.equals("420")) {
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
        } else if (queueId.equals("1700")) {
            result = "아레나";
        } else {
            result = "기타";
        }

        return result;
    }

}
