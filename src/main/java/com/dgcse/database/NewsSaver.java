package com.dgcse.database;

import com.dgcse.entity.AllNewsContent;
import com.dgcse.entity.NewsContent;
import com.dgcse.entity.NewsWordContent;
import org.hibernate.Session;
import org.hibernate.query.Query;

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

    public void insert_tbl_NewsContent(List<NewsContent> newsContentList) throws Exception{
        this.setUp();

        for(NewsContent newsContent : newsContentList){
            session.save(newsContent);
        }
        session.getTransaction().commit();
    }

    public void insert_tbl_AllNewsContent(List<AllNewsContent> allNewsContentList) throws  Exception{
        this.setUp();

        String insertDuplicate = "INSERT INTO tbl_allnews (realword, allcount) VALUES (?, ?) on duplicate key update allcount = allcount+?;";
        for(AllNewsContent a : allNewsContentList) {
            session.createNativeQuery(insertDuplicate)
                    .setParameter(1, a.getWord())
                    .setParameter(2, a.getAllcount())
                    .setParameter(3, a.getAllcount())
                    .executeUpdate();
        }
        // 저장할 entity 전부 save한 후 commit 해주면 DB에 반영
        session.getTransaction().commit();
    }

    public void insert_tbl_NewsWordContent(List<NewsWordContent> newsWordContentList) throws Exception{
        this.setUp();

        for(NewsWordContent newsWordContent : newsWordContentList){
            session.save(newsWordContent);
        }
        session.getTransaction().commit();
        session.close();
    }

    public int select_tbl_NewsContent(String url) throws Exception{
        this.setUp();

        Query query = session
                .createQuery("from tbl_news as n where n.url=?");
        query.setParameter(0,url);

        List<NewsContent> list = query.list();


        session.close();
        return list.get(0).getNid();
    }

    public int select_tbl_AllNewsContent(String word) throws Exception{
        this.setUp();

        // query문 사용시 원래 오류로 인식
        Query query = session
                .createQuery("from tbl_allnews as a where a.word=?");
        query.setParameter(0,word);

        List<AllNewsContent> list = query.list();

        session.close();
        return list.get(0).getWid();
    }
}
