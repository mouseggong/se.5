package com.dgcse;
import com.dgcse.crawler.entity.HttpResult;
import com.dgcse.crawler.module.SegyeParser;

import com.dgcse.database.Hibernate;
import com.dgcse.entity.AllNewsContent;
import com.dgcse.entity.NewsContent;
import com.dgcse.entity.NewsWordContent;
import com.dgcse.entity.NewsWordId;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HibernateTest {
    private Hibernate hibernate;
    private Session session;
    private static final String TEST_BASE_URL = "http://www.segye.com";

    // Test 전 session 생성
    @Before
    public void setUp() throws Exception{
        hibernate = Hibernate.getInstance();
        session = hibernate.getSessionFactory().getCurrentSession();
        session.beginTransaction();
    }

    // Test 후 session 제거
    @After
    public void tearDown() throws Exception{
        session.close();
        hibernate.getInstance().shutdown();
    }

    // tbl_news 에 대한 insert Test
    @Test
    @Ignore
    public void newsSaveTest()  throws Exception{
        SegyeParser test_parser = new SegyeParser();
        List<String>[] test_urlList = test_parser.oneWeek_urlList();
        test_parser.parsePage(TEST_BASE_URL+test_urlList[1].get(0));

        NewsContent newsContent = new NewsContent();
        newsContent.setTitle(test_parser.getTitle());
        newsContent.setBody(test_parser.getBody());
        newsContent.setDate(test_parser.getDate());
        newsContent.setReporter(test_parser.getReporter());
        newsContent.setUrl(TEST_BASE_URL+test_urlList[1].get(0));

        session.save(newsContent);
        session.getTransaction().commit();
    }

    // tbl_allnews 에 대한 insert Test
    @Test
    @Ignore
    public void insertDuplicateTest(){
        AllNewsContent allNewsContent1 = new AllNewsContent();
        allNewsContent1.setWord("test");
        allNewsContent1.setAllcount(10);
        AllNewsContent allNewsContent2 = new AllNewsContent();
        allNewsContent2.setWord("test");
        allNewsContent2.setAllcount(7);
        List<AllNewsContent> testList = new ArrayList<>();
        testList.add(allNewsContent1);
        testList.add(allNewsContent2);

        String insertDuplicate = "INSERT INTO tbl_allnews (realword, allcount) VALUES (?, ?) on duplicate key update allcount = allcount+?;";
        for(AllNewsContent a : testList) {
            session.createNativeQuery(insertDuplicate)
                    .setParameter(1, a.getWord())
                    .setParameter(2, a.getAllcount())
                    .setParameter(3, a.getAllcount())
                    .executeUpdate();
        }
        // 저장할 entity 전부 save한 후 commit 해주면 DB에 반영
        session.getTransaction().commit();
    }

    // tbl_News, tbl_AllNews 에 대한 select Test
    @Ignore
    @Test
    public void NewsWordInsertTest(){
        String url = "http://www.segye.com/content/html/2016/11/24/20161124004005.html";
        String word = "정부";

        // table 이름은 원래 오류로 인식
        Query query1 = session
                .createQuery("from tbl_news as n where n.url=?");
        query1.setParameter(0,url);

        List<NewsContent> list1 = query1.list();

        query1 = session
                .createQuery("from tbl_allnews as a where a.word=?");
        query1.setParameter(0,word);

        List<AllNewsContent> list2 = query1.list();


            System.out.println(list1.get(0).getNid());
            System.out.println(list2.get(0).getWid());
    }
}
