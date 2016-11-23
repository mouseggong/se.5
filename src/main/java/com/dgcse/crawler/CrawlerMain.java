//package com.dgcse.crawler;
//
////import com.dgcse.crawler.databases.Hibernate;
////import com.dgcse.crawler.databases.NewsSaver;
////import com.dgcse.crawler.entity.NewsContent;
//import com.dgcse.crawler.module.SegyeParser;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CralwerMain {
//    public static void main(String[] args) throws Exception{
//        SegyeParser parser = new SegyeParser();
//        List<String> urlList = parser.getNewsUrlListByDate("2016","11","22");
//        NewsSaver saver = new NewsSaver();
//        List<NewsContent> newsContentList = new ArrayList<>();
//        for(String url : urlList){
//            parser.parsePage("http://www.segye.com"+url);
//            newsContentList.add(new NewsContent(url,parser.getTitle(),parser.getBody(),parser.getDate(),parser.getReporter()));
//        }
//        saver.saveAllNews(newsContentList);
//        System.out.println(newsContentList.size()+"개의 데이터가 수집되었습니다.");
//        Hibernate.getInstance().shutdown();
//    }
//}