package com.fazendapro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FazendaProApplication {
    public static void main(String[] args) {
        SpringApplication.run(FazendaProApplication.class, args);
    }
}
