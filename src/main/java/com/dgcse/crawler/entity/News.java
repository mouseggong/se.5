package com.dgcse.crawler.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by moon-hochan on 2016-11-16.
 */
@Entity(name = "tbl_news")
public class News implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int nid;

    @Column(name="title",nullable = false)
    private String title;

    @Column(name="content")
    private String content;

    @Column(name="reporter",nullable = false)
    private String reporter;

    @Column(name="date",nullable = false)
    private String date;

    private int wordcount;

    public void setDate(String date) {
        this.date = date;
    }

    public int getWordcount() {
        return wordcount;
    }

    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }



    public News(){}

    public News(String title, String content, String reporter, String date) {
        this.title = title;
        this.content = content;
        this.reporter = reporter;
        this.date = date;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }


    @Override
    public String toString() {
        return "NewsVO [nid=" + nid + ", title=" + title + ", content="
                + content + ", reporter=" + reporter + ", date=" + date
                + "]";
    }
}
