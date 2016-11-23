package com.dgcse.entity;

import javax.persistence.*;

/**
 * Created by moon-hochan on 2016-11-22.
 * @Entity tbl_news 뉴스 기사
 * @Param nid 뉴스 기사 각각의 고유 번호
 * @Param title 제목
 * @Param body  본문
 * @Param date  날짜
 * @Param reporter 기자
 * @Param url 주소
 * @Param wordcount 뉴스 기사에 포함된 모든 단어의 수
 */
@Entity(name = NewsContent.TABLE_NAME)
public class NewsContent {

    public static final String TABLE_NAME = "tbl_news";
    private static final String COL_TITLE = "title";
    private static final String COL_BODY = "body";
    private static final String COL_DATE = "date";
    private static final String COL_REPORTER = "reporter";
    private static final String COL_URL = "url";
    private static final String COL_WORDCOUNT = "wordcount";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int nid;

    @Column(name = COL_TITLE,nullable = false)
    private String title;

    @Column(name=COL_BODY,columnDefinition="LONGTEXT")
    private String body;

    @Column(name=COL_DATE)
    private String date;

    @Column(name=COL_REPORTER)
    private String reporter;

    @Column(name=COL_URL)
    private String url;

    @Column(name=COL_WORDCOUNT)
    private int wordcount;

    public NewsContent(){

    }

    public NewsContent(String url,String title,String body,String date,String reporter, int wordcount){
        this.url = url;
        this.title = title;
        this.body = body;
        this.date = date;
        this.reporter = reporter;
        this.wordcount = wordcount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public int getWordcount() { return wordcount; }

    public void setWordcount(int wordcount) { this.wordcount = wordcount; }

}
