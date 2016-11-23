package com.dgcse.database;

import com.dgcse.entity.NewsContent;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by moon-hochan on 2016-11-22.
 */
public class NewsSaver {
    private Hibernate hibernate;
    private Session session;


    public void setUp() throws Exception{
        hibernate = Hibernate.getInstance();
        session = hibernate.getSessionFactory().getCurrentSession();
        session.beginTransaction();
    }

    public NewsSaver() throws Exception{
        try {
            hibernate = Hibernate.getInstance();
        }
        catch(Exception e){
            throw new Exception("데이터베이스 초기화 오류가 발생하였습니다.\nError Message : "+e.getMessage());
        }
    }

    public void saveAllNews(List<NewsContent> newsContentList) throws Exception{
        Session session = hibernate.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        for(NewsContent newsContent : newsContentList){
            session.save(newsContent);
        }
        session.getTransaction().commit();
    }

    public void insertIntoDuplicate(){
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

    public void newsSave(){
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
}
