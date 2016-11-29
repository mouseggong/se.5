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

/**
 * Created by moon-hochan on 2016-11-13.
 */

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
        SegyeParser test_parser;
        test_parser = new SegyeParser();
        test_parser.parsePage(TEST_BASE_URL+"/content/html/2016/11/26/20161126000909.html");

        NewsContent newsContent = new NewsContent();
        newsContent.setTitle(test_parser.getTitle());
        newsContent.setBody(test_parser.getBody());
        newsContent.setDate(test_parser.getDate());
        newsContent.setReporter(test_parser.getReporter());
        newsContent.setUrl(TEST_BASE_URL+"/content/html/2016/11/26/20161126000909.html");

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
    @Test
    @Ignore
    public void NewsWordInsertTest(){
        String url = TEST_BASE_URL+"/content/html/2016/11/22/20161122000222.html";
        //String word = "정부";

        // table 이름은 원래 오류로 인식
        Query query1 = session
                .createQuery("from tbl_news as n where n.url=?");
        query1.setParameter(0,url);

        List<NewsContent> list1 = query1.list();

        System.out.println(list1.get(0).getNid());
    }

    // 하나의 문서 내에서 전체 단어 개수
    @Test
    @Ignore
    public void totalPartialByNidTest(){
        int total = 0, i = 1;

        Query query = session
                .createNativeQuery("select partialcount from tbl_news_word where nid = ?");
        query.setParameter(1,i);

        List<Integer> list = query.list();

        for(int a : list){
            total += a;
        }

        System.out.println(total);
    }

    // 해당 테이블 전체 문서 개수
    @Ignore
    @Test
    public void IDF_total_doc(){
        String tbl = "tbl_news";

        Query query = session
                .createNativeQuery("select count(*) from " + tbl);

        List<Integer> list = query.list();

        System.out.println(list.get(0));
    }

    // 해당 단어를 포함하고 있는 문서의 개수
    @Test
    @Ignore
    public void IDF_contain_doc(){
        int i = 2;

        Query query = session
                .createNativeQuery("select count(if(wid=? , 1, null)) from tbl_news_word ");
        query.setParameter(1,i);

        List<Integer> list = query.list();

        System.out.println(list.get(0));
    }
}
