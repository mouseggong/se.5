package com.dgcse.entity;

import javax.persistence.*;

/**
 * Created by click on 2016-11-22.
 */
@Entity(name = AllNewsContent.TABLE_NAME )
public class AllNewsContent {

    public static final String TABLE_NAME = "tbl_allnews";
    private static final String COL_WORD = "realword";


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int wid;

    @Column(name = COL_WORD, unique = true)
    private String word;

    private int allcount;

    public AllNewsContent(){}

    public AllNewsContent(String word, int allcount) {
        this.word = word;
        this.allcount = allcount;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getAllcount() {
        return allcount;
    }

    public void setAllcount(int allcount) {
        this.allcount = allcount;
    }
}
