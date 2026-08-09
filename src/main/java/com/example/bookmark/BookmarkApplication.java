package com.example.bookmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BookmarkApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmarkApplication.class, args);
    }

}
