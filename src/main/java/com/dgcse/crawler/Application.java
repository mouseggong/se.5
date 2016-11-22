package com.dgcse.crawler;

import com.dgcse.crawler.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by moon-hochan on 2016-11-16.
 * spring boot 실행시 시작점
 */
@SpringBootApplication
public class Application{

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    NewsRepository newsRepository;

//  @Override
//  public void run(String[] args) throws Exception{
//
//  }
}

