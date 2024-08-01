package com.helipilatis.helipilatis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.nio.file.Paths;

@Configuration
public class DataSourceConfig {

    static {
        String projectLocation = Paths.get("").toAbsolutePath().toString();
        System.setProperty("project.location", projectLocation);
    }

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @PostConstruct
    public void init() {
        // Any additional initialization logic if needed
    }
}