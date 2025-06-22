package com.ashcollege.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class DbUtils {

    private Connection connection;

    @PostConstruct
    public void init () {
        createDbConnection();
    }

    private void createDbConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            String url = System.getenv("SPRING_DATASOURCE_URL");
            String username = System.getenv("SPRING_DATASOURCE_USERNAME");
            String password = System.getenv("SPRING_DATASOURCE_PASSWORD");

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connection successful to Supabase!");
        } catch (Exception e){
            System.out.println("❌ Cannot create DB connection!");
            e.printStackTrace();
        }
    }
}