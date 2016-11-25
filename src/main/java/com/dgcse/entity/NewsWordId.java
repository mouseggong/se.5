package com.dgcse.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Ianohjh on 2016-11-23.
 */
@Embeddable
public class NewsWordId implements Serializable {
    @Column(name = "nid", nullable = false)
    private int nid;
    @Column(name = "wid", nullable = false)
    private int wid;

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }
}
