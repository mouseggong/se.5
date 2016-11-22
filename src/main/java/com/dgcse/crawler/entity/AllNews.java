package com.dgcse.crawler.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by moon-hochan on 2016-11-17.
 */

@Entity(name = "tbl_allnews")
//@SQLInsert( sql="INSERT INTO tbl_allnews (realword, allcount) VALUES (? , ?) ON DUPLICATE KEY UPDATE allcount = allcount + 1")
public class AllNews implements Serializable{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int wid;

    @Column(name="realword", unique = true)
    private String realword;

    @Column(name="allcount")
    private int allcount;

    public AllNews(){}

    public AllNews(String realword, int allcount) {
        this.realword = realword;
        this.allcount=allcount;
    }

//    @Modifying
//    @SQLInsert( sql="INSERT INTO tbl_allnews(realword) VALUES (?) ON DUPLICATE KEY UPDATE allcount = allcount + 1")
//    void updateAllcount(String word){}

    public int getAllcount() {
        return allcount;
    }

    public void setAllcount(int allcount) {
        this.allcount = allcount;
    }



    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public String getRealword() {
        return realword;
    }

    public void setRealword(String realword) {
        this.realword = realword;
    }





    @Override
    public String toString() {
        return "AllNewssVO [wid=" + wid + ", realword=" + realword + ", Allcount="+ allcount
                + "]";
    }
}
