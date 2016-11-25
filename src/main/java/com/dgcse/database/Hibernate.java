package com.dgcse.database;

import com.dgcse.entity.AllNewsContent;
import com.dgcse.entity.NewsContent;
import com.dgcse.entity.NewsWordContent;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


import java.io.File;

/**
 * Created by moon-hochan on 2016-11-22.
 * @Param sessionFactory  session을 생성
 * @Param DEFAULT_CONFIG_FILE  Hibernate 설정 파일 path
 * @Param configuration  설정 정보가 담겨 있는 객체
 */
public class Hibernate {

    private static SessionFactory sessionFactory;
    private static String DEFAULT_CONFIG_FILE = "C:\\Users\\Ianohjh\\Desktop\\SE_Project\\src\\main\\resources\\hibernate.cfg.xml";
    private static Configuration configuration;
    private static Hibernate instance;

    // 설정 객체와 session Factory가 비어있을 경우 생성, 설정 객체를 통해 sessionFactory를 생성
    private Hibernate() throws Exception{
        if(configuration==null&&sessionFactory==null) {
            configuration = new Configuration().configure(new File(DEFAULT_CONFIG_FILE));
            configuration.addAnnotatedClass(NewsContent.class); // ?
            configuration.addAnnotatedClass(NewsWordContent.class);
            configuration.addAnnotatedClass(AllNewsContent.class);
            StandardServiceRegistryBuilder sb = new StandardServiceRegistryBuilder();
            sb.applySettings(configuration.getProperties());
            StandardServiceRegistry standardServiceRegistry = sb.build();
            sessionFactory = configuration.buildSessionFactory(standardServiceRegistry);
        }
    }

    public static Hibernate getInstance() throws Exception{
        if(instance==null)
            instance = new Hibernate();
        return instance;
    }

    // sessionFactory를 반환
    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    // DB와의 connection을 끊어 준다
    public void shutdown(){
        sessionFactory.close();
    }
}
