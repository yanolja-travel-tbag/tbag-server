package com.tbag.tbag_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class TbagBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TbagBackendApplication.class, args);
    }

}
