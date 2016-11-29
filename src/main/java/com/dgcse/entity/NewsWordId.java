package com.dgcse.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**.
 * Created by moon-hochan on 2016-11-19.
 * @Param nid 해당 뉴스기사의 고유 ID
 * @Param wid 해당 단어의 고유 ID
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
