package com.mathapp.auth.config;

import java.sql.Connection;

import javax.sql.DataSource;

import com.mathapp.auth.logging.LogService;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationStartupLogger {

    private final LogService logService;

    public ApplicationStartupLogger(LogService logService) {
        this.logService = logService;
    }

    @Bean
    public ApplicationRunner applicationRunner(DataSource dataSource) {

        return args -> {

            logService.info("=======================================");
            logService.info("MathApp Auth Backend Started");
            logService.info("=======================================");

            try (Connection connection = dataSource.getConnection()) {

                logService.info("Database Connection Successful");
                logService.info("URL      : {}", connection.getMetaData().getURL());
                logService.info("Database : {}", connection.getCatalog());
                logService.info("User     : {}", connection.getMetaData().getUserName());

            } catch (Exception ex) {

                logService.databaseFailed(ex);

            }

        };
    }
}
