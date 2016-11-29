package com.dgcse.entity;

import javax.persistence.*;

/**
 * Created by moon-hochan on 2016-11-19.
 * @Param   newsWordId      nid와 wid로 이루어진 객체(복합키)
 * @Param   partailcount    해당 문서에서 해당 단어의 개수
 */
@Entity(name = NewsWordContent.TABLE_NAME)
public class NewsWordContent {
    public static final String TABLE_NAME = "tbl_news_word";

    @Id
    @Embedded
    private NewsWordId newsWordId;

    @Column(name = "partialcount")
    private int partialcount;

    public NewsWordContent() {
    }

    public NewsWordContent(NewsWordId newsWordId, int partialcount) {
        this.newsWordId = newsWordId;
        this.partialcount = partialcount;
    }

    public NewsWordId getNewsWordId() {
        return newsWordId;
    }

    public void setNewsWordId(NewsWordId newsWordId) {
        this.newsWordId = newsWordId;
    }

    public int getPartialcount() {
        return partialcount;
    }

    public void setPartialcount(int partialcount) {
        this.partialcount = partialcount;
    }
}
