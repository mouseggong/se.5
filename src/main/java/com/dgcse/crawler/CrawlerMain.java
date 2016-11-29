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

/**
 * Created by Ianohjh on 2016-11-20.
 */

public class CrawlerMain {

    private static final String BASE_URL = "http://www.segye.com";

    //1번 Table에 Data를 Insert하는 메소드 (매개변수: 7일치 url List, parser, saver, 1번 테이블의 entity를 삽입할 List)
    static public void setup_1st_tbl(List<String>[] urlList, SegyeParser parser, NewsSaver saver, List<NewsContent> newsContentList) throws Exception{
        int index = 0;
        int count;

        while (urlList[index] != null) {
            count = 0;
            for (String url : urlList[index]) {
                parser.parsePage(BASE_URL + url);
                if(parser.check_NewsType()) { //일반 기사일때만 추가
                    newsContentList.add(new NewsContent(BASE_URL + url, parser.getTitle(), parser.getBody(), parser.getDate(), parser.getReporter(), parser.extractWordList(parser.getBody()).size(), parser.getPhoto_url()));
                    System.out.println("index = " + index + "   count = " + count++ + "  url: " + url);
                }
            }
            index++;
        }
        saver.insert_tbl_NewsContent(newsContentList);//saver insert메소드로 DB에 삽입
        System.out.println(newsContentList.size()+"개의 데이터가 수집되었습니다.");
    }

    //2번 Table에 Data를 Insert하는 메소드 (매개변수: 7일치 url List, parser, saver, 2번 테이블의 entity가 들어있는 List)
    static public void setup_2nd_tbl(List<String>[] urlList, SegyeParser parser, NewsSaver saver, List<AllNewsContent> allNewsContentList) throws Exception{
        int index = 0;
        int count;
        HashMap<String, Integer> word_hash;
        Set<String> key_set;

        while (urlList[index] != null) {
            count = 0;
            for (String url : urlList[index]) {
                parser.parsePage(BASE_URL + url);
                word_hash = parser.countWordinNews();
                key_set = word_hash.keySet();

                if(parser.check_NewsType()) { //일반 기사일때만 추가
                    for(String word : key_set){
                        allNewsContentList.add(new AllNewsContent(word, word_hash.get(word)));
                    }
                    System.out.println("index = " + index + "   count = " + count++ + "  url: " + url);
                }
            }
            index++;
        }
        saver.insert_tbl_AllNewsContent(allNewsContentList);//saver insert메소드로 DB에 삽입
        System.out.println(allNewsContentList.size()+"개의 데이터가 수집되었습니다.");
    }

    //3번 Table에 Data를 Insert하는 메소드 (매개변수: 7일치 url List, parser, saver, 3번 테이블의 entity가 들어있는 List)
    static public void setup_3rd_tbl(List<String>[] urlList, SegyeParser parser, NewsSaver saver, List<NewsWordContent> newsWordContentList) throws Exception{
        int index = 0;
        int count;
        HashMap<String, Integer> word_hash;
        Set<String> key_set;

        while (urlList[index] != null) {
            count = 0;
            for (String url : urlList[index]) {
                parser.parsePage(BASE_URL + url);
                word_hash = parser.countWordinNews();
                key_set = word_hash.keySet();

                if(parser.check_NewsType()){ //일반 기사일때만 추가
                    for(String word : key_set){
                        /**
                         * 3번 테이블에 Insert
                         * NewsWordId entity를 Key로 사용하여 NewsWordContent를 구성한다.
                         */
                        NewsWordId newsWordId = new NewsWordId(); // 3번 테이블 키
                        newsWordId.setNid(saver.select_tbl_NewsContent(BASE_URL + url));
                        newsWordId.setWid(saver.select_tbl_AllNewsContent(word));
                        newsWordContentList.add(new NewsWordContent(newsWordId,word_hash.get(word)));
                    }
                    System.out.println("index = " + index + "   count = " + count++ + "  url: " + url);
                }
            }
            index++;
        }
        saver.insert_tbl_NewsWordContent(newsWordContentList);//saver insert메소드로 DB에 삽입
        System.out.println(newsWordContentList.size()+"개의 데이터가 수집되었습니다.");
    }

    //main 함수 시작
    public static void main(String[] args) throws Exception{
        SegyeParser parser = new SegyeParser();
        List<String>[] urlList = parser.oneWeek_urlList();

        NewsSaver saver = new NewsSaver(); //DB 쿼리 관련 메소드를 사용하기 위한 saver객체 생성
        List<NewsContent> newsContentList = new ArrayList<>();//1번 테이블 List
        List<AllNewsContent> allNewsContentList = new ArrayList<>(); //2번 테이블 List
        List<NewsWordContent> newsWordContentList = new ArrayList<>(); //3번 테이블 List

        setup_1st_tbl(urlList,parser,saver,newsContentList);//1번 테이블 -> Data Insert 메소드
        setup_2nd_tbl(urlList,parser,saver,allNewsContentList);//2번 테이블 -> Data Insert 메소드
        setup_3rd_tbl(urlList,parser,saver,newsWordContentList);//3번 테이블 -> Data Insert 메소드
        saver.update_TF();//3번 테이블의 TF값을 계산하여 Update시키는 메소드
        Hibernate.getInstance().shutdown();//Hibernate 닫기
    }
}
