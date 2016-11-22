package com.dgcse.crawler.repository;

import com.dgcse.crawler.entity.AllNews;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by moon-hochan on 2016-11-20.
 */
public interface AllNewsRepository extends CrudRepository<AllNews, Integer> {

//    @Modifying
//    @SQLInsert( sql="INSERT INTO tbl_allnews(realword) VALUES (?) ON DUPLICATE KEY UPDATE allcount = allcount + 1")
//    void upsert(String word);

    @Modifying
    @Transactional
    @Query(value="insert into tbl_allnews (realword) values (?1) on duplicate key update allcount=allcount+1",nativeQuery = true)
    void updateAllcount (String realword);

}
