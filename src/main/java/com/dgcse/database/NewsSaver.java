package com.dgcse.database;

import com.dgcse.entity.AllNewsContent;
import com.dgcse.entity.NewsContent;
import com.dgcse.entity.NewsWordContent;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moon-hochan on 2016-11-18.
 *  DB query 관련 메소드
 *  update, select, insert 기능
 */

public class NewsSaver {
    private Hibernate hibernate;
    private Session session;

    // 세션을 생성하고 DB와의 transaction을 시작해 주는 메소드
    public void setUp() throws Exception{
        hibernate = Hibernate.getInstance();
        session = hibernate.getSessionFactory().getCurrentSession();
        session.beginTransaction();
    }

    // NewsSaver 생성자 (hibernate instance를 생성한다.)
    public NewsSaver() throws Exception{
        try {
            hibernate = Hibernate.getInstance();
        }
        catch(Exception e){
            throw new Exception("데이터베이스 초기화 오류가 발생하였습니다.\nError Message : "+e.getMessage());
        }
    }

    // tbl_news 에 뉴스기사를 insert 해주는 메소드
    public void insert_tbl_NewsContent(List<NewsContent> newsContentList) throws Exception{
        this.setUp();
        boolean flag = true;
        // 크롤링한 뉴스기사들이 저장되어있는 newsContentList를 session에 저장
        for(NewsContent newsContent : newsContentList){
            session.save(newsContent);
        }
        while(flag) { // DB에 commit 될때 까지 반복
            try {
                session.getTransaction().commit();
                flag = false;
            } catch (PersistenceException e) {
                System.out.println("PersistenceException 1st table");
            } catch (IllegalStateException e) {
                System.out.println("IllegalStateException 1st table");
            }
        }
    }

    // tbl_allnews 에 단어가 있을 경우 count를 update 없을 경우 insert 해주는 메소드
    public void insert_tbl_AllNewsContent(List<AllNewsContent> allNewsContentList) throws  Exception{
        this.setUp();
        boolean flag = true;
        // insert into on duplicate update 쿼리 구문
        String insertDuplicate = "INSERT INTO tbl_allnews (realword, allcount) VALUES (?, ?) on duplicate key update allcount = allcount+?;";
        for(AllNewsContent a : allNewsContentList) {
            session.createNativeQuery(insertDuplicate)
                    .setParameter(1, a.getWord())
                    .setParameter(2, a.getAllcount())
                    .setParameter(3, a.getAllcount())
                    .executeUpdate(); // 업데이트 실행
        }
        while(flag) { // commit 될 때 까지 반복
            try {
                session.getTransaction().commit();
                flag = false;
            } catch (PersistenceException e) {
                System.out.println("PersistenceException 2nd table");
            } catch (IllegalStateException e) {
                System.out.println("IllegalStateException 2nd table");
            }
        }
    }

    // tbl_news_word 에 문서당 개별 단어의 개수를 insert 해주는 메소드
    public void insert_tbl_NewsWordContent(List<NewsWordContent> newsWordContentList) throws Exception{
        this.setUp();
        boolean flag = true;
        // 문서당 개별 단어의 개수가 저장되어 있는 newsWordContentList를 session에 save
        for(NewsWordContent newsWordContent : newsWordContentList){
            session.save(newsWordContent);
        }
        while(flag) { // commit 될때까지 반복
            try {
                session.getTransaction().commit();
                session.close();
                flag = false;
            } catch (PersistenceException e) {
                System.out.println("PersistenceException 3rd table");
            } catch (IllegalStateException e) {
                System.out.println("IllegalStateException 3rd table");
            }
        }
    }

    // tbl_news 에서 해당 url을 가지는 Row의 nid를 반환하는 메소드
    public int select_tbl_NewsContent(String url) throws Exception{
        this.setUp();
        List<NewsContent> list = new ArrayList<>();
        boolean flag = true;
        int nid = 0;
        while (flag) { //  nid를 얻어올 때 까지 반복
            try {
                // url 이 일치하는 Row를 받아온다
                Query query = session
                        .createQuery("from tbl_news as n where n.url=?");
                query.setParameter(0, url);

                list = query.list();
                // 받아온 Row의 nid 추출
                nid = list.get(0).getNid();
                flag = false;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("IndexOutOfBoundsException :  Index: 0, Size: 0");
            }
            catch (IllegalStateException e){
                System.out.println("IllegalStateException: Session/EntityManager is closed");
            }
        }
        session.close();
        return nid;
    }

    // tbl_allnews 에서 해당 단어의 wid를 반환하는 메소드
    public int select_tbl_AllNewsContent(String word) throws Exception{
        this.setUp();
        boolean flag = true;
        List<AllNewsContent> list = new ArrayList<>();
        int wid = 0;
        while (flag) {
            try {
                // tbl_allnews 에서 word가 일치하는 Row를 받아온다
                Query query = session
                        .createQuery("from tbl_allnews as a where a.word=?");
                query.setParameter(0, word);

                list = query.list();
                // Row에서 wid 추출
                wid = list.get(0).getWid();
                flag = false;
            }
            catch (IndexOutOfBoundsException e){
                System.out.println("IndexOutOfBoundsException :  Index: 0, Size: 0");
            }
            catch (IllegalStateException e){
                System.out.println("IllegalStateException: Session/EntityManager is closed");
            }
        }
        session.close();
        return wid;
    }

    // tbl_news와 tbl_news_word를 nid로 조인하여 tf 값을 update 하여 주는 메소드
    public void update_TF() throws Exception{
        this.setUp();
        boolean flag = true;
        while(flag) {
            try {
                /**
                 * round (A.partialcount/B.wordcount , 5)  : TF 값을 구하여 5째 자리수까지 반올림
                 * TF - 하나의 문서에서 해당 단어의 개수/ 하나의 문서에서 전체 단어의 개수
                 */
                Query query = session
                        .createNativeQuery("update tbl_news_word A inner join tbl_news B on A.nid = B.nid set A.tf = round(A.partialcount/B.wordcount, 5)");
                query.executeUpdate();
                session.getTransaction().commit();
                session.close();
                flag = false;
            }
            catch (IndexOutOfBoundsException e){
                System.out.println("IndexOutOfBoundsException :  Index: 0, Size: 0");
            }
            catch (IllegalStateException e){
                System.out.println("IllegalStateException: Session/EntityManager is closed");
            }
            catch (PersistenceException e) {
                System.out.println("PersistenceException: Error Check ");
            }
        }
    }
}
