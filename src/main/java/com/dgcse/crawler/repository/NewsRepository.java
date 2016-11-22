package com.dgcse.crawler.repository;

import com.dgcse.crawler.entity.News;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NewsRepository extends CrudRepository<News, Integer> {

    List<News> findByReporter(String reporter);
}