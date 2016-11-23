package com.dgcse;

import com.dgcse.database.Hibernate;
import com.dgcse.entity.NewsContent;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by moon-hochan on 2016-11-22.
 */
public class HibernateTest {
    private Hibernate hibernate;
    private Session session;

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
    @Ignore
    @Test
    public void newsSaveTest(){
        NewsContent newsContent = new NewsContent();
        newsContent.setTitle("test title");
        newsContent.setBody("test body");
        newsContent.setDate("19931128");
        newsContent.setReporter("aa");
        newsContent.setUrl("www.hochans.home");
        session.save(newsContent);
        newsContent.setTitle("halu");
        session.save(newsContent);
        session.getTransaction().commit();
    }

    // tbl_allnews 에 대한 insert Test
    @Ignore
    @Test
    public void insertDuplicateTest(){
        String list[] = {"hochan", "hochan1"};

        String insertDuplicate = "INSERT INTO tbl_allnews (realword, allcount) VALUES (?, 1) on duplicate key update allcount = allcount+1;";
        for(String realword : list) {
            session.createNativeQuery(insertDuplicate)
                    .setParameter(1, realword)
                    .executeUpdate();
        }
        // 저장할 entity 전부 save한 후 commit 해주면 DB에 반영
        session.getTransaction().commit();
    }

}
