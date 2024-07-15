package com.umiverse.umiversebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.umiverse.umiversebackend.repository.mongodb")
@EnableJpaRepositories(basePackages = "com.umiverse.umiversebackend.repository.mysql")
public class UmiVerseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UmiVerseBackendApplication.class, args);
    }

}
