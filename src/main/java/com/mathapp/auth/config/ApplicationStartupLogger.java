package com.mathapp.auth.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationStartupLogger {

    private static final Logger log =
            LoggerFactory.getLogger(ApplicationStartupLogger.class);

    @Bean
    public ApplicationRunner applicationRunner(DataSource dataSource) {

        return args -> {

            log.info("=======================================");
            log.info("MathApp Auth Backend Started");
            log.info("=======================================");

            try (Connection connection = dataSource.getConnection()) {

                log.info("Database Connection Successful");
                log.info("URL      : {}", connection.getMetaData().getURL());
                log.info("Database : {}", connection.getCatalog());
                log.info("User     : {}", connection.getMetaData().getUserName());

            } catch (Exception ex) {

                log.error("Database Connection Failed");

                log.error(ex.getMessage(), ex);

            }

        };
    }
}