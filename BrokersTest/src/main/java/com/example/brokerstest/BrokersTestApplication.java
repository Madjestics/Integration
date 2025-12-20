package com.example.brokerstest;

import com.example.commontest.config.CorsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CorsConfig.class)
public class BrokersTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrokersTestApplication.class, args);
    }

}
