package com.dgcse.crawler;


import com.dgcse.database.Hibernate;
import com.dgcse.database.NewsSaver;
import com.dgcse.entity.AllNewsContent;
import com.dgcse.entity.NewsContent;
import com.dgcse.crawler.module.SegyeParser;
import com.dgcse.entity.NewsWordContent;
import com.dgcse.entity.NewsWordId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CrawlerMain {

    public static void main(String[] args) throws Exception{
        SegyeParser parser = new SegyeParser();
        List<String>[] urlList = parser.oneWeek_urlList();
        HashMap<String, Integer> word_hash;

        NewsSaver saver = new NewsSaver();
        List<NewsContent> newsContentList = new ArrayList<>();//1번 테이블 List
        List<AllNewsContent> allNewsContentList = new ArrayList<>(); //2번 테이블 List
        List<NewsWordContent> newsWordContentList = new ArrayList<>(); //3번 테이블 List
        Set<String> key_set;

        for(int i = 0; i < urlList.length; i++) {
            for (int j = 0; j < urlList[i].size(); j++) {
                parser.parsePage("http://www.segye.com" + urlList[0].get(j));
//                word_hash = parser.countWordinNews();
//                key_set = word_hash.keySet();
                if(parser.check_NewsType()) { //일반 기사일때만 추가
                    newsContentList.add(new NewsContent("http://www.segye.com" + urlList[i].get(j), parser.getTitle(), parser.getBody(), parser.getDate(), parser.getReporter(), parser.extractWordList(parser.getBody()).size(), parser.getPhoto_url()));
                    System.out.println("i = " + i + "   j = " + j);
                }
            }
        }
        saver.insert_tbl_NewsContent(newsContentList);
        System.out.println(newsContentList.size()+"개의 데이터가 수집되었습니다.");
        Hibernate.getInstance().shutdown();



//        for(int i = 0; i < urlList.length; i++) {
//            for (int j = 0; j < urlList[i].size(); j++) {
//                parser.parsePage("http://www.segye.com" + urlList[0].get(j));
//                word_hash = parser.countWordinNews();
//                key_set = word_hash.keySet();
//
//                for(String word : key_set){
//                    allNewsContentList.add(new AllNewsContent(word, word_hash.get(word)));
//                }
//                if(parser.check_NewsType()){
//                    for(String word : key_set){
//                        NewsWordId newsWordId = new NewsWordId(); // 3번 테이블 키
//                        newsWordId.setNid(saver.select_tbl_NewsContent("http://www.segye.com" + urlList[0].get(j)));
//                        newsWordId.setWid(saver.select_tbl_AllNewsContent(word));
//                        newsWordContentList.add(new NewsWordContent(newsWordId,word_hash.get(word)));
//                    }
//                }
//            }
//        }
//
//        saver.insert_tbl_AllNewsContent(allNewsContentList);
//        System.out.println(allNewsContentList.size()+"개의 데이터가 수집되었습니다.");
//        saver.insert_tbl_NewsWordContent(newsWordContentList);
//        System.out.println(newsWordContentList.size()+"개의 데이터가 수집되었습니다.");
//        Hibernate.getInstance().shutdown();
    }
}