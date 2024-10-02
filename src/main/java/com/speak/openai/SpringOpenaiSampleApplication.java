package com.speak.openai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication public class SpringOpenaiSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringOpenaiSampleApplication.class, args);
    }

}
