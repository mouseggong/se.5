package com.dgcse.crawler;

/**
 * Created by click on 2016-11-22.
 */
import com.dgcse.crawler.entity.AllNews;
import com.dgcse.crawler.entity.News;
import com.dgcse.crawler.repository.AllNewsRepository;
import com.dgcse.crawler.repository.NewsRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;


/**
 * Created by click on 2016-11-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class DbTest {
    @Autowired
    NewsRepository newsRepository;
    @Autowired
    AllNewsRepository allNewsRepository;

    @Before
    public void setUp() throws Exception{
//        newsRepository.save(new News("title","content","reporter","19931128"));
    }

    @Ignore
    @Test
    public void search() throws Exception{
        Iterable<News> list1 = newsRepository.findAll();

        System.out.println("findAll() Method.");
        for( News m : list1){
            System.out.println(m.toString());
        }

        System.out.println("findByReporter() Method.");
        List<News> list2 = newsRepository.findByReporter("hochan");
        for( News m : list2){
            System.out.println(m.toString());
        }
    }

    @Test
    public void InesertorUpdate() throws Exception{
        String word = "hochan1";
        AllNews allNews = new AllNews(word,0);
        allNewsRepository.updateAllcount(word);
        allNewsRepository.updateAllcount(word);
        allNewsRepository.updateAllcount(word);

    }
}
